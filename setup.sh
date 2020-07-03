#!/usr/bin/env bash
## author: Mohamed Taman
## version: v4.5
echo -e "\nInstalling all Springy store core shared modules & Parent POMs"
echo -e  "...............................................................\n"
echo "1- Installing [Parent Build Chassis] module..."
./mvnw --quiet clean install -U -pl store-base/store-build-chassis || exit 126
echo -e "Done successfully.\n"
echo "2- Installing [Parent Cloud Chassis] module..."
./mvnw --quiet clean install -U -pl store-base/store-cloud-chassis || exit 126
echo -e "Done successfully.\n"
echo "3- Installing shared [Services Utilities] module..."
./mvnw --quiet clean install -pl store-common/store-utils || exit 126
echo -e "Done successfully.\n"
echo "4- Installing shared [Services APIs] module..."
./mvnw --quiet clean install -pl store-common/store-api || exit 126
echo -e "Done successfully.\n"
echo "5- Installing [Services Parent Chassis] module..."
./mvnw --quiet clean install -U -pl store-base/store-service-chassis || exit 126
echo -e "Done successfully.\n"

echo -e "Woohoo, building & installing all project modules are finished successfully.\n\
The project is ready for the next step. :)"