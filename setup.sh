#!/usr/bin/env bash
## author: Mohamed Taman
## version: v1.0
echo -e "\nInstalling all Springy store core shared modules"
echo -e  "................................................\n"
echo "1- Installing [build parent] module..."
./mvnw --quiet clean install -pl store-build-chassis || exit 126
echo -e "Done successfully.\n"
echo "2- Installing shared [Utilities] module..."
./mvnw --quiet clean install -pl store-utils || exit 126
echo -e "Done successfully.\n"
echo "3- Installing shared [APIs] module..."
./mvnw --quiet clean install -pl store-api || exit 126
echo -e "Done successfully.\n"
echo "4- Installing [service parent] module..."
./mvnw --quiet clean install -pl store-service-chassis || exit 126
echo -e "Done successfully.\n"

echo -e "Woohoo, building & installing all project modules are finished successfully.\n\
The project is ready for the next step. :)"