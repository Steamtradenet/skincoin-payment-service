#!/bin/sh

/contract/expect.sh | sed -rn "s/(.*SkinCoin address: )(0x[a-f0-9]{40})/\2/p" > /gethdata/SkinCoin.address


