.PHONY: clean

COMPILER=mvn
FLAGS="-DskipTests"
default: all
install: all

all: 
	$(COMPILER) $(FLAGS) package
	@echo
	@echo "See final ZIP artifact in target/"
	@echo

test:
	$(COMPILER) package
 
clean:
	$(COMPILER) clean
