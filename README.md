# HKBU-Search-Engine

Search engine written for a group project in CSC 4047 Internet and World Wide Web taken at Hong Kong Baptist University. 

Consists of a web crawler and a server.

The web crawler uses jsoup to connect to websites and saves information on website contents in an inverted index
implemented with a hashmap for fast lookup times. Word position within the website is saved as well. A set of arrays 
hold information including total text length of each site, site title in metadata if present, and a calculated PageRank score.

Server uses the Spring Framework to accept queries and return results. Data files created by the crawler are deserialized from
storage. 
