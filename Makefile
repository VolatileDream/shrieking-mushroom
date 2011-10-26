
#setup source files for use as a library
SOURCE = $(shell find ./src/core ./src/protocol ./src/networking -type f -iname '*.java' -print | sed 's_/src__' )

# setup source files for use with an executable demo
DEMO_SOURCE = $(shell echo "$(SOURCE)" | sed 's_*demoApp*__' )

# make everything, package into 2 jar files ( one with demo, one without )
all : run bin
	@echo "Making library..."
	@cd bin ; jar cf ./shrieking-mushroom.jar $(SOURCE:java=class)
	@echo "Making demo..."
	@cd bin ; jar cfe ./shrieking-mushroom-demo.jar demoApp.run $(DEMO_SOURCE:java=class)

./bin :
	@echo "Creating binary directory..."
	@mkdir ./bin
	@echo "Compiling source files..."
	@cd src ; javac -g -d ./../bin $(DEMO_SOURCE)

./run :
	@echo "Creating running environment..."
	@mkdir ./run
	@mkdir ./run/res

clean : 
	@echo "Removing compiled files"
	@rm ./bin -r
