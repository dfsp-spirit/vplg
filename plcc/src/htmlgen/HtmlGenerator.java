package htmlgen;

public class HtmlGenerator {

	protected ArrayList<File> cssFiles;
	protected HashMap<String, File> graphTypeFiles;
	protected String pdbid;
	
	public String generateProteinChainWebpage(String pdbid, List<String> chains, HashMap<String, File> graphTypeFiles) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.generateProteinChainPageHeader);
		return sb.toString();
	}

	private String generateProteinChainPageHeader() {
		String title = "VPLG -- PDB " + this.pdbid + " --";
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<title>" + title + "</title>\n");
		// append CSS and other stuff here
		sb.append("</head>\n");
		return sb.toString();
	}

	
}
