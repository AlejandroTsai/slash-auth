echo "[INFO] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 开始构建骨架 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
mvn clean
mvn archetype:create-from-project
echo "[INFO] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 骨架构建完毕 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"

echo "[INFO] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 开始进入骨架目录 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
cd target/generated-sources/archetype/
mvn install
mvn archetype:crawl
echo "[INFO] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 骨架安装到本地成功！！！如下是骨架版本号 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
cat "${MAVEN_HOME}/LocalHouse/archetype-catalog.xml"


# 使用命令创建骨架项目
# mvn archetype:generate
# --它会让你选择你需要的maven骨架，然后输入groupId，artifactId, version, package。之后就在你所在的目录创建了maven项目--
# groupId: com.heycine.slash.xxx
# artifactId: slash-xxx
# version: 1.0.0
# package: com.heycine.slash.xxx

# 发布和使用远程仓库
# mvn clean deploy
# mvn archetype:generate -DarchetypeCatelog=http:localhost:8080/archetype-catalog.xml        远程