#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v1.0
### Sample usage:
#
#   HOST=localhost PORT=9080 ./test-em-all.bash
#
echo -e "Starting [Springy Store] full functionality testing....\n"

: ${HOST=localhost}
: ${PORT=9080}

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
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [[ "$actual" = "$expected" ]]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

# Verify that a normal request works, expect three recommendations and three reviews
assertCurl 200 "curl http://$HOST:$PORT/product-composite/1 -s"
assertEqual 1 $(echo ${RESPONSE} | jq .productId)
assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that a 404 (Not Found) error is returned for a non existing productId (13)
assertCurl 404 "curl http://$HOST:$PORT/product-composite/13 -s"

# Verify that no recommendations are returned for productId 113
assertCurl 200 "curl http://$HOST:$PORT/product-composite/113 -s"
assertEqual 113 $(echo ${RESPONSE} | jq .productId)
assertEqual 0 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 3 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that no reviews are returned for productId 213
assertCurl 200 "curl http://$HOST:$PORT/product-composite/213 -s"
assertEqual 213 $(echo ${RESPONSE} | jq .productId)
assertEqual 3 $(echo ${RESPONSE} | jq ".recommendations | length")
assertEqual 0 $(echo ${RESPONSE} | jq ".reviews | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/product-composite/-1 -s"
assertEqual "\"Invalid productId: -1\"" "$(echo ${RESPONSE} | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a productId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/product-composite/invalidProductId -s"
assertEqual "\"Type mismatch.\"" "$(echo ${RESPONSE} | jq .message)"

