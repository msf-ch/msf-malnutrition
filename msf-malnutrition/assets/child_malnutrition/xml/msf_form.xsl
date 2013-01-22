<?xml version="1.0" encoding="UTF-8"?>
<!-- DWXMLSource="child_form.xml" -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><xsl:value-of select="title"/></title>
    <link href="../jquery-mobile/jquery.mobile-1.1.0.min.css" rel="stylesheet" type="text/css"/>
    <link href="../css/msf-style.css" rel="stylesheet" type="text/css"/>
    <link href="../css/datebox.css"  rel="stylesheet" type="text/css"/>
    <script src="../jquery/jquery-1.8.3.min.js" type="text/javascript" ></script>
    <script src="../jquery-mobile/jquery.mobile-1.1.0.min.js" type="text/javascript"></script>
    <script src="../javascript/datebox.core.js" type="text/javascript"></script>
    <script src="../javascript/flipbox.js" type="text/javascript"></script>
    <script src="../javascript/datebox.en.utf8.js" type="text/javascript"></script>
    </head>
    <body>
    <xsl:apply-templates select="htmlform/pages/page"/>
    
    </body>
    </html>
  </xsl:template>
  <xsl:template match="page">
    <div data-role="page">
      <div data-role="header" data-theme="b" data-position="fixed" data-tap-toggle="false">
        <h1><xsl:value-of select="@header"/></h1>
      </div>
      <div data-role="content">
        <xsl:apply-templates />
      </div>
    </div>
  </xsl:template>
  <xsl:template match="plainhtml">
    <xsl:copy-of select="./*" />
  </xsl:template>
  
  <!--  <xsl:template match="datebox">
        <label></label>
        <xsl:><input name="suividat" id="suividat" type="text" data-role="datebox" data-options='{"mode": "flipbox", "headerFormat":"dd mmm, YYYY", "focusMode": true, "beforeToday":true, "useClearButton":true, "collapseButtons": true}' placeholder="Date"/></xsl:text>
  </xsl:template> -->
  
  <xsl:template name="//labeledNode">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="labelText">
    <xsl:element name="label">
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="textEntry">
  	<xsl:element name="input">
    	<xsl:attribute name="type">text</xsl:attribute>
    </xsl:element>
  </xsl:template>
  
  <xsl:attribute-set name="dataHolder">
    <xsl:attribute name="concept"><xsl:value-of select="@concept"/></xsl:attribute>
    <xsl:attribute name="conceptType"><xsl:value-of select="@conceptType"/></xsl:attribute>
  </xsl:attribute-set>
  
</xsl:stylesheet>