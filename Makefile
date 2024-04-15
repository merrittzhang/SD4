# COS 445 SD4, Spring 2019
# Created by Andrew Wonnacott

all: Auctioneer.class

sd4.zip: Auctioneer.java AuctionConfig.java Makefile Bidder.java Bidder_truthful.java Tournament.java bidders.txt
	zip -j sd4 Auctioneer.java AuctionConfig.java Makefile Bidder.java Bidder_truthful.java Tournament.java bidders.txt README.txt

test: results.csv
	@cat results.csv

results.csv: all bidders.txt
	java -ea Auctioneer bidders.txt > results.csv

Auctioneer.class: Auctioneer.java *.java
	javac -Xlint Auctioneer.java *.java

#bidders.txt: Bidder_*.java
#	@touch bidders.txt
#	@while [[ `wc -l < bidders.txt` -lt 12 ]]; do    ls | grep -e 'Bidder_.*\.java' | sed s/.*Bidder_// | sed s/\\.java$$// >> bidders.txt; done

clean:
	rm -rf *.class results.csv sd4.zip

upload: sd4.zip
	scp sd4.zip cos445@cycles.cs.princeton.edu:~/../htdocs/cos445/sd4.zip
