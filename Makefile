
# setup source files for use with an executable demo
BUILD_SOURCE = $(shell find ./src -type f -iname '*.java' -print | sed 's_/src__' )

JAR = shrieking-mushroom

# make everything, package into 2 jar files ( one with demo, one without )
all : run bin
	@echo "Making library..."
	@cd bin ; jar cf ./$(JAR).jar $$(find shriekingMushroom -type f )
	@echo "Making demo..."
	@cd bin ; jar cfe ./$(JAR)-demo.jar demoApp.run $$(find demoApp/ shriekingMushroom/ -type f )
	@cp bin/$(JAR).jar run/ ; cp bin/$(JAR)-demo.jar run/

./bin :
	@echo "Creating binary directory..."
	@mkdir ./bin
	@echo "Compiling source files..."
	@cd src ; javac -g -d ./../bin $(BUILD_SOURCE)

./run :
	@echo "Creating running environment..."
	@mkdir ./run
	@mkdir ./run/res

clean : 
	@echo "Removing compiled files"
	@rm ./bin -r
	@rm ./run -r
