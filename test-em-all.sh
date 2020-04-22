#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v4.0
### Sample usage:
#
#   for local run
#     HOST=localhost PORT=9080 ./test-em-all.bash
#   with docker compose
#     HOST=localhost PORT=8080 ./test-em-all.bash start stop
#
echo -e "Starting [Springy Store] full functionality [Blackbox] testing....\n"

: ${HOST=localhost}
: ${PORT=8080}
: ${PROD_ID_REVS_RECS=2}
: ${PROD_ID_NOT_FOUND=14}
: ${PROD_ID_NO_RECS=114}
: ${PROD_ID_NO_REVS=214}

BASE_URL="/store/api/v1/products"

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval ${curlCmd})
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [[ "$httpCode" = "$expectedHttpCode" ]]
  then
    if [[ "$httpCode" = "200" ]]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
    return 0
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      return 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [[ "$actual" = "$expected" ]]
  then
    echo "Test OK (actual value: $actual)"
    return 0
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    return 1
  fi
}

function testUrl() {
    url=$@
    if curl ${url} -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl ${url}
    do
        n=$((n + 1))
        if [[ ${n} == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

function testCompositeCreated() {

    # Expect that the Product Composite for productId $PROD_ID_REVS_RECS
    # has been created with three recommendations and three reviews
    if ! assertCurl 200 "curl http://${HOST}:${PORT}${BASE_URL}/${PROD_ID_REVS_RECS} -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$PROD_ID_REVS_RECS" $(echo ${RESPONSE} | jq .productId)
    if [[ "$?" -eq "1" ]] ; then return 1; fi

    assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
    if [[ "$?" -eq "1" ]] ; then return 1; fi

    assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")
    if [[ "$?" -eq "1" ]] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ ${n} == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local productId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://${HOST}:${PORT}${BASE_URL}/${productId} -s"
    curl -X POST http://${HOST}:${PORT}${BASE_URL} -H "Content-Type: application/json" --data "$composite"
}

function setupTestData() {

    body="{\"productId\":$PROD_ID_NO_RECS"
    body+=\
',"name":"product name A","weight":100, "reviews":[
    {"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
    {"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
    {"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
]}'
    recreateComposite "$PROD_ID_NO_RECS" "$body"

    body="{\"productId\":$PROD_ID_NO_REVS"
    body+=\
',"name":"product name B","weight":200, "recommendations":[
    {"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
    {"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
    {"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
]}'
    recreateComposite "$PROD_ID_NO_REVS" "$body"


    body="{\"productId\":$PROD_ID_REVS_RECS"
    body+=\
',"name":"product name C","weight":300, "recommendations":[
        {"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
        {"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
        {"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
    ], "reviews":[
        {"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
        {"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
        {"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
    ]}'

    recreateComposite 1 "$body"
}

set -e

echo "Start Tests:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose -p ssm down --remove-orphans"
    docker-compose -p ssm down --remove-orphans
    echo "$ docker-compose -p ssm up -d"
    docker-compose -p ssm up -d
fi

waitForService curl http://${HOST}:${PORT}/actuator/health

setupTestData

waitForMessageProcessing

# Verify that a normal request works, expect three recommendations and three reviews
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL}/$PROD_ID_REVS_RECS -s"
assertEqual ${PROD_ID_REVS_RECS} $(echo ${RESPONSE} | jq .productId)
assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that a 404 (Not Found) error is returned for a non existing productId (13)
assertCurl 404 "curl http://$HOST:$PORT${BASE_URL}/$PROD_ID_NOT_FOUND -s"

# Verify that no recommendations are returned for productId 113
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL}/$PROD_ID_NO_RECS -s"
assertEqual ${PROD_ID_NO_RECS} $(echo ${RESPONSE} | jq .productId)
assertEqual 0 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that no reviews are returned for productId 213
assertCurl 200 "curl http://$HOST:$PORT${BASE_URL}/$PROD_ID_NO_REVS -s"
assertEqual ${PROD_ID_NO_REVS} $(echo ${RESPONSE} | jq .productId)
assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 0 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT${BASE_URL}/-1 -s"
assertEqual "\"Invalid productId: -1\"" "$(echo ${RESPONSE} | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a productId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT${BASE_URL}/invalidProductId -s"
assertEqual "\"Type mismatch.\"" "$(echo ${RESPONSE} | jq .message)"

echo "End, all tests OK:" `date`

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
     echo "$ docker-compose down --remove-orphans"
    docker-compose -p ssm down --remove-orphans
fi