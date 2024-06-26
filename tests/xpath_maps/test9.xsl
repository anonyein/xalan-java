<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="map"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                  			                 				
   
   <!-- An XSLT test case to test, XPath 3.1 map constructor expression
        that nests another map. -->
        
   <xsl:output method="xml" indent="yes"/> 
   
   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
	  <xsl:variable name="map1" select="map {
										    'one' : map {
											           'x1' : 'p',
													   'x2' : 'q',
													   'x3' : 'r',
							                           'x4' : 's',
							                           'x5' : 't' 						   
											        },
											'two' : 2,
											'three' : [5, 6, 7, 8, 9, 'a1'] }"/>
      <xsl:variable name="innerMap" select="map:get($map1, 'one')"/>											
      <innerMap noOfEntries="{count(map:keys($innerMap))}"/>
   </xsl:template>

</xsl:stylesheet>
