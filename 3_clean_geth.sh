docker-compose stop geth
docker-compose rm -vf geth
sudo rm -r /mnt/skincoin/geth
cd docker/geth
docker build -t skincoin/geth .
cd ../..
