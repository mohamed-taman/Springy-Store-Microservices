#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v5.0
### Sample usage:
#
#   for local run
#     HOST=localhost PORT=8443 ./test-em-all.bash
#   with docker compose
#     HOST=localhost PORT=8443 ./test-em-all.bash start stop
#
echo -e "Starting 'Springy Store μServices' for [end-2-end] testing....\n"

: ${HOST=localhost}
: ${PORT=8443}
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
          return 0
    else
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
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo -e "\n DONE, continues...\n"
}

function testCompositeCreated() {

    # Expect that the Product Composite for productId $PROD_ID_REVS_RECS
    # has been created with three recommendations and three reviews
    if ! assertCurl 200 "curl $AUTH -k https://${HOST}:${PORT}${BASE_URL}/${PROD_ID_REVS_RECS} -s"
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

    assertCurl 200 "curl $AUTH -X DELETE -k https://${HOST}:${PORT}${BASE_URL}/${productId} -s"
    curl -X POST -k https://${HOST}:${PORT}${BASE_URL} -H "Content-Type: application/json" -H \
    "Authorization: Bearer $ACCESS_TOKEN" \
    --data "$composite"
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

waitForService curl -k https://${HOST}:${PORT}/actuator/health

ACCESS_TOKEN=$(curl -k https://writer:secret@${HOST}:${PORT}/oauth/token \
                    -d grant_type=password -d username=taman -d password=password -s \
                     | jq .access_token -r)

AUTH="-H \"Authorization: Bearer $ACCESS_TOKEN\""

setupTestData

waitForMessageProcessing

# Verify that a normal request works, expect three recommendations and three reviews
assertCurl 200 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_REVS_RECS $AUTH -s"
assertEqual ${PROD_ID_REVS_RECS} $(echo ${RESPONSE} | jq .productId)
assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that a 404 (Not Found) error is returned for a non existing productId (13)
assertCurl 404 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_NOT_FOUND $AUTH -s"

# Verify that no recommendations are returned for productId 113
assertCurl 200 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_NO_RECS $AUTH -s"
assertEqual ${PROD_ID_NO_RECS} $(echo ${RESPONSE} | jq .productId)
assertEqual 0 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that no reviews are returned for productId 213
assertCurl 200 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_NO_REVS $AUTH -s"
assertEqual ${PROD_ID_NO_REVS} $(echo ${RESPONSE} | jq .productId)
assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 0 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)
assertCurl 422 "curl -k https://$HOST:$PORT${BASE_URL}/-1 $AUTH -s"
assertEqual "\"Invalid productId: -1\"" "$(echo ${RESPONSE} | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a productId that is not a number, i.e. invalid format
assertCurl 400 "curl -k https://$HOST:$PORT${BASE_URL}/invalidProductId $AUTH -s"
assertEqual "\"Type mismatch.\"" "$(echo ${RESPONSE} | jq .message)"

# Verify that a request without access token fails on 401, Unauthorized
assertCurl 401 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_REVS_RECS -s"

# Verify that the reader - client with only read scope can call the read API but not delete API.
READER_ACCESS_TOKEN=$(curl -k https://reader:secret@${HOST}:${PORT}/oauth/token \
                           -d grant_type=password -d username=taman -d password=password -s | \
                            jq .access_token -r)

READER_AUTH="-H \"Authorization: Bearer $READER_ACCESS_TOKEN\""

assertCurl 200 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_REVS_RECS $READER_AUTH -s"
assertCurl 403 "curl -k https://$HOST:$PORT${BASE_URL}/$PROD_ID_REVS_RECS $READER_AUTH -X DELETE -s"

echo "End, all tests OK:" `date`

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose -p ssm down --remove-orphans"
    docker-compose -p ssm down --remove-orphans
fi