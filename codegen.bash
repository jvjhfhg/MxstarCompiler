set -e
cd "$(dirname "$0")"
export mxstar="java -classpath ./lib/antlr-4.7.2-complete.jar:./bin mxstar.Compiler"
cat > program.cpp
$mxstar
