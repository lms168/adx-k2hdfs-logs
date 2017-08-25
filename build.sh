#!/bin/bash

set -e

if [ ! $1 ]; then
    echo "error!"
    exit 1
fi

export HADOOP_USER_NAME=root

# timestamp=$(date +%s%3N)

d=$(date -u +"%Y-%m-%dT%H%M%SZ")

sbt -Dsbt.override.build.repos=true -Dconf=$1 clean pkg

HDFS="hdfs://192.168.0.61:9000/ci"

HTTP="http://192.168.0.61:50070/explorer.html#/ci"

p1=${CI_PROJECT_NAMESPACE}/${CI_PROJECT_NAME}/${CI_COMMIT_REF_NAME}/${CI_COMMIT_SHA}/$1

p=${HDFS}/${p1}

localdir=/home/output/$p1

cd ./target/universal/

for i in $(ls . | grep -v SHA1SUMS)
do
    if [ -f $i ] ; then
        echo $(sha1sum $i) >> SHA1SUMS
    fi
done

cd -

mkdir -pv $localdir

cp -vr target/universal/* $localdir/

/opt/hadoop/bin/hdfs dfs -mkdir -p $p

touch target/universal/${d}.txt

rm -vfr target/universal/tmp

/opt/hadoop/bin/hdfs dfs -copyFromLocal -f target/universal/* $p/

echo "build completed."
echo "packages in local: $localdir"
echo "packages in hdfs: $p"
echo "packages in http: ${HTTP}/${p1}"

exit 0
