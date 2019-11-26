<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://www.esei.uvigo.es/dai/hybridserver"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.ei.uvigo.es/dai/hybridserver configuration.xsd">

	<xsl:output method="html" encoding="utf8" indent="yes" />

	<xsl:template match="/">
		<html>
			<head>
				<title>Configuración</title>
			</head>
			<body>
				<div id="container">
					<xsl:apply-templates
						select="c:configuration/c:connections" />
					<xsl:apply-templates
						select="c:configuration/c:database" />
					<xsl:apply-templates
						select="c:configuration/c:servers" />
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="c:connections">
		<h3>Connections</h3>
		<div>
			<div class="http">
				<strong>HTTP:</strong>
				&#160;
				<xsl:value-of select="c:http" />
			</div>
			<div class="webservice">
				<strong>Web Service:</strong>
				&#160;
				<xsl:value-of select="c:webservice" />
			</div>
			<div class="numClients">
				<strong>Number of clients:</strong>
				&#160;
				<xsl:value-of select="c:numClients" />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="c:database">
		<h3>Database</h3>
		<div>
			<div class="user">
				<strong>User:</strong>
				&#160;
				<xsl:value-of select="c:user" />
			</div>
			<div class="password">
				<strong>Password:</strong>
				&#160;
				<xsl:value-of select="c:password" />
			</div>
			<div class="url">
				<strong>URL:</strong>
				&#160;
				<xsl:value-of select="c:url" />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="c:servers">
		<h3>Servers</h3>
		<div>
			<xsl:for-each select="c:server">
				<div class="server">
					<strong>Server name:</strong>
					&#160;
					<xsl:value-of select="@name" />
					&#160;
					<strong>WSDL:</strong>
					&#160;
					<xsl:value-of select="@wsdl" />
					&#160;
					<strong>Namespace:</strong>
					&#160;
					<xsl:value-of select="@namespace" />
					&#160;
					<strong>Service:</strong>
					&#160;
					<xsl:value-of select="@service" />
					&#160;
					<strong>HTTP Address:</strong>
					&#160;
					<xsl:value-of select="@httpAddress" />
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
</xsl:stylesheet>