# Smart XML Analyzer
<b>How To:</b>

Application requires only two mandatory params: 
1) Path to original file with prototype of element to match. Id of element should be 'make-everything-ok-button'.
2) Path to diff file with relevant element to compare.

Third parameter is optional - id of prototype element, if you want something different rather than case mentioned in path to original file.

To run execute following command in console within project directory:

java -jar crawler-0.1-SNAPSHOT.jar %path-to-original% %path-to-diff% %id-of-prototype-element%(Optional)

Enjoy :)