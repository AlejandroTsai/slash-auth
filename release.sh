#!/bin/bash

echo "[INFO] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 开始构建【正式】版本 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"

newProjectVersion=$1

projectVersion=$(./mvnw -q  -Dexec.executable="echo"  -Dexec.args='${project.version}'  --non-recursive  org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)

echo "[INFO] ===================================== 当前版本:${projectVersion}  ====================================="
# shellcheck disable=SC2162
#read -p "请输入新的版本号:" newProjectVersion

echo "[INFO] ===================================== 输入版本${newProjectVersion} ====================================="

if [ "$newProjectVersion" == "" ]; then
    echo "[INFO] ===================================== 输入版本号为空，使用当前版本【${projectVersion}】 ====================================="
    newProjectVersion=${projectVersion}
fi
versionArray=(${newProjectVersion//-/ })

#版本号字符串
versionNoStr=${versionArray[0]}

#版本号数组
versionNoArray=(${versionNoStr//./ })

#最后一位版本号，用于自增版本
lastVersion=${versionNoArray[2]}

#自增后最后一位版本号
newLastVersion=$( expr ${lastVersion} + 1 )

#新版本号
newVersion="${versionNoArray[0]}.${versionNoArray[1]}.${newLastVersion}"

echo "[INFO] ===================================== 开始替换版本【${newVersion}】 ====================================="

./mvnw clean versions:set -DnewVersion=${newVersion} -N versions:update-child-modules

./mvnw clean package deploy --settings ./.mvn/wrapper/settings.xml

echo "[INFO] ===================================== 新版本${newVersion} ====================================="

echo "[INFO] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 构建完毕 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
