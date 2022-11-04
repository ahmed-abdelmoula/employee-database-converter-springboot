package com.project.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TableToXML {
	@Autowired
	private DataSource dataSource;
	Document doc = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	DOMSource domSource = null;
	Connection cn = null;
	InputStream is=null;

	public InputStream generateXML(String tablename ) throws TransformerException,
	ParserConfigurationException {
			
			 cn = DataSourceUtils.getConnection(dataSource);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			 doc = builder.newDocument();
			Element results = doc.createElement("Table");
			doc.appendChild(results);

			try {
			pstmt = cn.prepareStatement("select * from "+tablename+";");

			rs = pstmt.executeQuery();

			System.out.println("Col count pre ");
			ResultSetMetaData rsmd = rs.getMetaData();//to retrieve table name, column name, column type and column precision, etc..
			int colCount = rsmd.getColumnCount();

			Element tableName = doc.createElement("TableName");
			tableName.appendChild(doc.createTextNode(rsmd.getTableName(1)));
			results.appendChild(tableName);

			Element structure = doc.createElement("TableStructure");
			results.appendChild(structure);

			Element col = null;
			for (int i = 1; i <= colCount; i++) {

				col = doc.createElement("Column" + i);
				results.appendChild(col);
				Element columnNode = doc.createElement("ColumnName");
				columnNode
						.appendChild(doc.createTextNode(rsmd.getColumnName(i)));
				col.appendChild(columnNode);

				Element typeNode = doc.createElement("ColumnType");
				typeNode.appendChild(doc.createTextNode(String.valueOf((rsmd
						.getColumnTypeName(i)))));
				col.appendChild(typeNode);

				Element lengthNode = doc.createElement("Length");
				lengthNode.appendChild(doc.createTextNode(String.valueOf((rsmd
						.getPrecision(i)))));
				col.appendChild(lengthNode);

				structure.appendChild(col);
			}

			System.out.println("Col count = " + colCount);

			Element productList = doc.createElement("TableData");
			results.appendChild(productList);

			int l = 0;
			while (rs.next()) {
				Element row = doc.createElement("Product" + (++l));
				results.appendChild(row);
				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode((value != null) ? value
							.toString() : ""));
					row.appendChild(node);
				}
				productList.appendChild(row);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Source xmlSource = new DOMSource(doc);
			Result outputTarget = new StreamResult(outputStream);
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
			 is = new ByteArrayInputStream(outputStream.toByteArray());
			
		/*	domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);*/
			
		

		//	System.out.println("Xml document 1" + sw.toString());

			System.out.println("********************************");

		} catch (SQLException sqlExp) {

			System.out.println("SQLExcp:" + sqlExp.toString());

		} finally {
			try {

				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (cn != null) {
					cn.close();
					cn = null;
				}
			} catch (SQLException expSQL) {
				System.out
						.println("CourtroomDAO::loadCourtList:SQLExcp:CLOSING:"
								+ expSQL.toString());
			}
		}

		// return sw.toString();

		return is;

	}
}
