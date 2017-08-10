#!/bin/sh

echo "Ethereum network: $NETWORK" > NETWORK

if [ $NETWORK == "live" ]; then
  geth --ipcpath /gethdata/geth.ipc --rpc --rpcaddr 0.0.0.0 --rpcapi "eth,net,web3,personal,admin" --datadir /gethdata
elif [ $NETWORK == "test" ]; then
  if [ ! -d /gethdata/keystore ]; then
    geth --datadir /gethdata --password /etc/geth/passwordfile account new \
        | grep -o -E "[a-f0-9]{40}" | awk '{print "0x"$1}' > /gethdata/Payout.address
  fi

  geth --ipcpath /gethdata/geth.ipc --rpc --rpcaddr 0.0.0.0 \
       --unlock 0 --password /etc/geth/passwordfile \
       --mine --minerthreads 1 --maxpeers 0 \
       --rpcapi "eth,net,web3,personal,admin" --datadir /gethdata --testnet

else
  if [ ! -d /gethdata/keystore ]; then
    geth --datadir /gethdata --password /etc/geth/passwordfile account new \
        | grep -o -E "[a-f0-9]{40}" | awk '{print "0x"$1}' > /gethdata/Payout.address
  fi

  geth --ipcpath /gethdata/geth.ipc --rpc --rpcaddr 0.0.0.0 \
       --unlock 0 --password /etc/geth/passwordfile \
       --mine --minerthreads 1 --maxpeers 0 \
       --rpcapi "eth,net,web3,personal,admin" --datadir /gethdata --networkid 55566 --dev
fi