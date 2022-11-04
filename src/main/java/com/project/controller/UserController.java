package com.project.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.project.dao.UserRep;
import com.project.model.FileUpload;
import com.project.model.User;
import com.project.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserRep userep;
	@Autowired
	private UserService userService;

	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	Job job;

	List<String> selectnames;

	@GetMapping("/alluser")
	@ResponseBody
	public List<User> alluser() {
		return userep.findAll();
	}

	@GetMapping("/access-denied")
	public String accessDenied() {
		return "/error/access-denied";
	}

	@GetMapping("/")
	public String showPages(Model model, @RequestParam(defaultValue = "0") int page) {
		List<String> tablesnames = new ArrayList<String>();
		tablesnames.addAll(jdbcTemplate.queryForList("show tables;", String.class));
		model.addAttribute("data", userep.findAll(PageRequest.of(page, 6)));
		model.addAttribute("currentPage", page);
		model.addAttribute("tablesname", tablesnames);
		return "index";
	}

	@GetMapping("/tst")
	public String d(Model model) throws SQLException {
		List<String> testname = new ArrayList<String>();

		Connection cn = DataSourceUtils.getConnection(dataSource);
		Statement stmt = cn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM country;");
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String name = rsmd.getColumnName(i);
			testname.add(name);

		}
		int rowCount = 0;
		ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();
		ArrayList<String> singleList = null;

		while (rs.next()) {
			singleList = new ArrayList<String>();

			rowCount++;
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				singleList.add(rs.getString(i + 1));

			}
			listOLists.add(singleList);

		}

		model.addAttribute("lama", listOLists);

		model.addAttribute("testname", testname);
		return "tst";
	}

	@RequestMapping(value = "/personnes", produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	@ResponseBody
	public List<User> getAllpersonnes() {
		return userep.findAll();
	}

	@PostMapping("/save")
	public String createUser(User u) {
		userep.save(u);
		return "redirect:/";
	}

	@GetMapping("/findOne")

	@ResponseBody
	public User findOne(Integer id) {
		return userep.findById(id).orElse(null);
	}

	@GetMapping("/XmlOne")

	@ResponseBody
	public User XmlOne(Integer id) {
		return userep.findById(id).orElse(null);
	}

	@GetMapping("/delete")
	public String deleteUser(Integer id) {
		userep.deleteById(id);
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login(Integer id) {
		return "login";

	}

	private static String UPLOADED_FOLDER = "C:\\Users\\ahmed\\eclipse-workspace\\autretest\\src\\main\\resources\\";
	public static String ch = "";

	@PostMapping("/upload") // //new annotation since 4.3 public String
	public String singleFileUpload(@RequestParam("file") MultipartFile file, Model model)
			throws SQLException, ParserConfigurationException, SAXException {

		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			ch = UPLOADED_FOLDER + file.getOriginalFilename();
			Files.write(path, bytes);
			System.out.println("fssdfsd");
			datatoxml();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	public void datatoxml() throws SQLException, ParserConfigurationException, SAXException, IOException {

		System.out.println("dqdsdfsdfds");
		Connection cn = null;
		cn = DataSourceUtils.getConnection(dataSource);
		Statement st=cn.createStatement();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		System.out.println(ch);

		Document inputDocument = docBuilder.parse(new File(ch));


		inputDocument.getDocumentElement().normalize();
		Node carsNode = inputDocument.getFirstChild();
		NodeList carsNodeList = carsNode.getChildNodes();
		//System.out.println(carsNode.toString());
		for (int i = 0; i < carsNodeList.getLength(); i++) {
			Node node = carsNodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				 Vector<String> data =new Vector();

				Element e = (Element) node;
				//System.out.println("Node " + e.getTextContent()+"gfgdf");
				NodeList childNodeList = e.getChildNodes();
                //System.out.println("CarType: " + node.getNodeName());

				for (int j = 0; j < childNodeList.getLength(); j++) {

					Node childNode = childNodeList.item(j);
		            if(childNode.hasChildNodes()) {
		            	 String sch = childNode.getFirstChild().getTextContent();
		            	data.add(sch);
		            	
		            System.out.println("siz"+data.size());
		         System.out.println(childNode.getFirstChild().getTextContent());
				          // st.executeUpdate("insert into "+node.getNodeName() +" values ('"+name+"','"+address+"')");

		            }

				}
	            System.out.println("size"+data.size());
	            ch="";

				for(int k=0;k<data.size();k++)
				{
					System.out.println(data.elementAt(k));
					ch+="'"+data.elementAt(k)+"' ,";
					

				}
		        ch = ch.substring(0, ch.length() - 1);

				System.out.println(ch);
				System.out.println(node.getNodeName());
		        st.executeUpdate("insert into "+node.getNodeName() +" values (  " +ch+ " ) ;");

				System.out.println("***************************");
				 
			}
		}

		
	}

	@RequestMapping(value = "/xmltod", method = RequestMethod.GET)
	// @ResponseBody
	public String databasetoxml(@RequestParam String ch)
			throws IOException, TransformerException, ParserConfigurationException, SAXException {
		Document doc = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DOMSource domSource = null;
		Connection cn = null;
		InputStream is = null;
		cn = DataSourceUtils.getConnection(dataSource);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.newDocument();
		/*
		 * Element results = doc.createElement("Table"); doc.appendChild(results);
		 */

		try {
			pstmt = cn.prepareStatement("select * from " + ch + ";");

			rs = pstmt.executeQuery();

			System.out.println("Col count pre ");
			ResultSetMetaData rsmd = rs.getMetaData();// to retrieve table name, column name, column type and column
														// precision, etc..
			int colCount = rsmd.getColumnCount();
			/*
			 * Element tableName = doc.createElement("TableName");
			 * tableName.appendChild(doc.createTextNode(rsmd.getTableName(1)));
			 * results.appendChild(tableName);
			 * 
			 * Element structure = doc.createElement("TableStructure");
			 * results.appendChild(structure);
			 */
			// Element col = null;
			/*
			 * for (int i = 1; i <= colCount; i++) {
			 * 
			 * col = doc.createElement("Column" + i); results.appendChild(col); Element
			 * columnNode = doc.createElement("ColumnName"); columnNode
			 * .appendChild(doc.createTextNode(rsmd.getColumnName(i)));
			 * col.appendChild(columnNode);
			 * 
			 * Element typeNode = doc.createElement("ColumnType");
			 * typeNode.appendChild(doc.createTextNode(String.valueOf((rsmd
			 * .getColumnTypeName(i))))); col.appendChild(typeNode);
			 * 
			 * Element lengthNode = doc.createElement("Length");
			 * lengthNode.appendChild(doc.createTextNode(String.valueOf((rsmd
			 * .getPrecision(i))))); col.appendChild(lengthNode);
			 * 
			 * structure.appendChild(col); }
			 */

			System.out.println("Col count = " + colCount);
			/*
			 * Element results = doc.createElement("Table"); doc.appendChild(results);
			 * Element productList = doc.createElement(ch + "s");
			 * results.appendChild(productList);
			 */

			Element productList = doc.createElement(ch + "s");
			doc.appendChild(productList);

			int l = 0;
			while (rs.next()) {
				// Element row = doc.createElement(ch + (++l));
				Element row = doc.createElement(ch);

				productList.appendChild(row);
				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode((value != null) ? value.toString() : ""));
					row.appendChild(node);
				}
				productList.appendChild(row);
			}
			/*
			 * ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); Source
			 * xmlSource = new DOMSource(doc); Result outputTarget = new
			 * StreamResult(outputStream);
			 * TransformerFactory.newInstance().newTransformer().transform(xmlSource,
			 * outputTarget); is = new ByteArrayInputStream(outputStream.toByteArray());
			 */

			/*
			 * domSource = new DOMSource(doc); TransformerFactory tf =
			 * TransformerFactory.newInstance(); Transformer transformer =
			 * tf.newTransformer();
			 * transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			 * transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			 * transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			 * 
			 * StringWriter sw = new StringWriter(); StreamResult sr = new StreamResult(sw);
			 * transformer.transform(domSource, sr);
			 */

			// System.out.println("Xml document 1" + sw.toString());

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
				System.out.println("CourtroomDAO::loadCourtList:SQLExcp:CLOSING:" + expSQL.toString());
			}
		}

		/*
		 * File file =new File("C:\\Users\\ahmed\\Desktop\\etud.xml");
		 * System.out.println("mmmmm"); OutputStream outputStream = new
		 * FileOutputStream(file); IOUtils.copy(is, outputStream); outputStream.close();
		 * InputStreamResource resource = new InputStreamResource(new
		 * FileInputStream(file));
		 */
		DOMSource source = new DOMSource(doc);
		FileWriter writer = new FileWriter(new File("C:\\Users\\ahmed\\Desktop\\etud.xml"));
		StreamResult result = new StreamResult(writer);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);

		Path path = Paths.get("C:\\Users\\ahmed\\Desktop\\etud.xml");
		byte[] data = Files.readAllBytes(path);
		ByteArrayResource resource = new ByteArrayResource(data);
		// InputStream raw = new FileInputStream(new
		// File("C:\\Users\\ahmed\\Desktop\\etud.xml"));
		System.out.println("makmak");

		// DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		// DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		/*
		 * Document doc = docBuilder.parse(new
		 * File("C:\\Users\\ahmed\\Desktop\\etud.xml"));
		 */
		// Document lac = docBuilder.parse(raw);

		/*
		 * return ResponseEntity.ok() .header(HttpHeaders.CONTENT_DISPOSITION,
		 * "attachment;filename=" + path.getFileName().toString())
		 * .contentType(MediaType.APPLICATION_XML).contentLength(data.length)
		 * .body(resource);
		 */
		return "redirect:/user?ch=" + ch;

	}

	// @RequestMapping(value = "/xmltod", method = RequestMethod.GET)
	// @ResponseBody
	public String databasetoxm(@RequestParam String ch)
			throws IOException, TransformerException, ParserConfigurationException {
		/*
		 * TableToXML tbx=new TableToXML(); InputStream inputStream =
		 * tbx.generateXML(ch);
		 */
		Document doc = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DOMSource domSource = null;
		Connection cn = null;
		InputStream is = null;
		cn = DataSourceUtils.getConnection(dataSource);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.newDocument();
		Element results = doc.createElement("Table");
		// doc.appendChild(results);

		try {
			pstmt = cn.prepareStatement("select * from " + ch + ";");

			rs = pstmt.executeQuery();

			System.out.println("Col count pre ");
			ResultSetMetaData rsmd = rs.getMetaData();// to retrieve table name, column name, column type and column
														// precision, etc..
			int colCount = rsmd.getColumnCount();
			/*
			 * Element tableName = doc.createElement("TableName");
			 * tableName.appendChild(doc.createTextNode(rsmd.getTableName(1)));
			 * results.appendChild(tableName);
			 * 
			 * Element structure = doc.createElement("TableStructure");
			 * results.appendChild(structure);
			 */
			Element col = null;
			/*
			 * for (int i = 1; i <= colCount; i++) {
			 * 
			 * col = doc.createElement("Column" + i); results.appendChild(col); Element
			 * columnNode = doc.createElement("ColumnName"); columnNode
			 * .appendChild(doc.createTextNode(rsmd.getColumnName(i)));
			 * col.appendChild(columnNode);
			 * 
			 * Element typeNode = doc.createElement("ColumnType");
			 * typeNode.appendChild(doc.createTextNode(String.valueOf((rsmd
			 * .getColumnTypeName(i))))); col.appendChild(typeNode);
			 * 
			 * Element lengthNode = doc.createElement("Length");
			 * lengthNode.appendChild(doc.createTextNode(String.valueOf((rsmd
			 * .getPrecision(i))))); col.appendChild(lengthNode);
			 * 
			 * structure.appendChild(col); }
			 */

			System.out.println("Col count = " + colCount);

			Element productList = doc.createElement(ch + "s");
			results.appendChild(productList);

			int l = 0;
			while (rs.next()) {
				Element row = doc.createElement(ch + (++l));
				results.appendChild(row);
				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode((value != null) ? value.toString() : ""));
					row.appendChild(node);
				}
				productList.appendChild(row);
			}

			/*
			 * ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); Source
			 * xmlSource = new DOMSource(doc); Result outputTarget = new
			 * StreamResult(outputStream);
			 * TransformerFactory.newInstance().newTransformer().transform(xmlSource,
			 * outputTarget); is = new ByteArrayInputStream(outputStream.toByteArray());
			 */
			/*
			 * File file =new File("C:\\Users\\ahmed\\Desktop\\etud.xml");
			 * System.out.println("mmmmm"); OutputStream outputStream = new
			 * FileOutputStream(file); IOUtils.copy(is, outputStream); outputStream.close();
			 */

			/*
			 * domSource = new DOMSource(doc); TransformerFactory tf =
			 * TransformerFactory.newInstance(); Transformer transformer =
			 * tf.newTransformer();
			 * transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			 * transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			 * transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			 * 
			 * StringWriter sw = new StringWriter(); StreamResult sr = new StreamResult(sw);
			 * transformer.transform(domSource, sr);
			 */

			// write the content into xml file

			// System.out.println("Xml document 1" + sw.toString());

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
				System.out.println("CourtroomDAO::loadCourtList:SQLExcp:CLOSING:" + expSQL.toString());
			}
		}

		// return sw.toString();

		/*
		 * InputStreamResource resource = new InputStreamResource(new
		 * FileInputStream(file)); return
		 * ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
		 * "attachment;filename=" + file.getName())
		 * .contentType(MediaType.APPLICATION_XML).contentLength(file.length()).body(
		 * resource);
		 */
		/*
		 * return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
		 * "attachment;filename=" + file.getName())
		 * .contentType(MediaType.APPLICATION_XML).contentLength(file.length()).body(
		 * resource);
		 */
		return "redirect:/user?ch=" + ch;

	}

	@GetMapping("/user")
	public String selectOption(Model model, @RequestParam String ch) throws SQLException {
		selectnames = new ArrayList<String>();

		Connection cn = DataSourceUtils.getConnection(dataSource);
		Statement stmt = cn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + ch + ";");
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String name = rsmd.getColumnName(i);
			selectnames.add(name);
			System.out.println("hetha tag" + ch);

		}
		model.addAttribute("selectnames", selectnames);

		return "newpage";
	}

	@RequestMapping(value = { "/functionlist/save" }, method = RequestMethod.POST)
	public String deleteElement(@RequestParam List<String> searchValues, @RequestParam String lx)
			throws ParserConfigurationException, IOException, TransformerException, SAXException {
		InputStream raw = new FileInputStream(new File("C:\\Users\\ahmed\\Desktop\\etud.xml"));
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(raw);
		List<String> diff = org.apache.commons.collections4.ListUtils.subtract(selectnames, searchValues);
		System.out.println(Arrays.toString(searchValues.toArray()));
		System.out.println(Arrays.toString(selectnames.toArray()));
		System.out.println(Arrays.toString(diff.toArray()));
		System.out.println("hetha tag" + lx);
		// Node someNode = doc.getDocumentElement();
		// System.out.println(Arrays.toString(searchValues.toArray()));
		// org.w3c.dom.NodeList childs = someNode.getChildNodes();
		NodeList nodeList = doc.getElementsByTagName(lx);
		// Element rootElement = doc.getDocumentElement();
//	    NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element e = (Element) node;
				// System.out.println("Node " + e.getTextContent());

				NodeList childNodeList = e.getChildNodes();
				for (int h = 0; h < diff.size(); h++) {
					String ach = diff.get(h);

					for (int j = 0; j < childNodeList.getLength(); j++) {
						Node childNode = childNodeList.item(j);
						System.out.println("array diff:" + ach);
						System.out.println("CHILD NODE" + childNode.getNodeName());

						if (childNode.getNodeName().equalsIgnoreCase(ach)) {
							childNode.getParentNode().removeChild(childNode);

							// continue;
						} else {
							System.out.println("PAS EQUIV");
							// deleteElement(child);
						}
					}

				}

			}

		}

		doc.getDocumentElement().normalize();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\Users\\ahmed\\Desktop\\testsup.xml"));

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
		System.out.println("XML file updated successfully");
		return "redirect:/filesc";
	}

	@RequestMapping(value = "/filesc", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadFile2() throws IOException, Exception {
		/*
		 * JobParametersBuilder builder = new JobParametersBuilder();
		 * builder.addDate("date", new Date()); jobLauncher.run(job,
		 * builder.toJobParameters());
		 */

		File file = new File("C:\\Users\\ahmed\\Desktop\\testsup.xml");
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
				.contentType(MediaType.APPLICATION_XML).contentLength(file.length()).body(resource);
	}

	@PostMapping("/doUpload")
	public String doupload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttribut)
			throws IOException, JAXBException {
		// System.out.println(fileUpload.getFiles());
		redirectAttribut.addFlashAttribute("filesNames", uploadandImportDb(file));
		System.out.println("fkdf");
		return "redirect:/";
	}
	/*
	 * @RequestMapping(value = "/files", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public ResponseEntity<InputStreamResource> downloadFile1()
	 * throws IOException, Exception { JobParametersBuilder builder = new
	 * JobParametersBuilder(); builder.addDate("date", new Date());
	 * jobLauncher.run(job, builder.toJobParameters());
	 * 
	 * File file = new File(
	 * "C:\\Users\\ahmed\\eclipse-workspace\\autretest\\target\\classes\\etud.xml");
	 * InputStreamResource resource = new InputStreamResource(new
	 * FileInputStream(file));
	 * 
	 * return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
	 * "attachment;filename=" + file.getName())
	 * .contentType(MediaType.APPLICATION_XML).contentLength(file.length()).body(
	 * resource); }
	 */

	@GetMapping("/uploadPage")
	public ModelAndView uploadPage() {
		ModelAndView model = new ModelAndView("index");
		FileUpload formupload = new FileUpload();
		model.addObject("formupload", formupload);
		return model;
	}
//	@RequestMapping(value = { "/functionlist/save" }, method = RequestMethod.POST)
//	public String deleteElement(@RequestParam List<String> searchValues)

	private List<String> uploadandImportDb(MultipartFile fileUpload) throws IOException, JAXBException {
		List<String> fileNames = new ArrayList<String>();
		List<String> paths = new ArrayList<String>();

		/*
		 * CommonsMultipartFile[] comm=fileUpload.getFiles();
		 * System.out.println("salah"+comm);
		 */

		String filepath = "";
		// for(CommonsMultipartFile multipartFile : comm) {
		filepath = "C:\\Users\\ahmed\\Desktop\\Semester 1\\JEE-Spring\\" + fileUpload.getOriginalFilename();
		File file = new File(filepath);
		// copy files
		FileCopyUtils.copy(fileUpload.getBytes(), file);
		fileNames.add(fileUpload.getOriginalFilename());
		paths.add(filepath);
		// }
		// process parse import data
		userService.process(paths);
		return fileNames;

	}
}
