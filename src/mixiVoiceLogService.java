import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * @author udumge
 * mixi�{�C�X���擾����T�[�r�X
 */
public class mixiVoiceLogService {

	//mixi�̃��O�C��URL
	private String loginURL = "http://mixi.jp/login.pl?from=login1";

	//mixi���O�C�����̃��t���b�V���i�Ȃɂ���H�j
	private String refleshURL = "http://mixi.jp/check.pl?n=%2Fhome.pl";

	//mixi�̃{�C�XURL
	private String voiceRecentURL = "http://mixi.jp/recent_voice.pl";

	private String loginId;
	private String password;

	// ���O�C��ID��Form�ł̃p�����[�^��
	private String loginIdParamName = "email";
	// �p�X���[�h��Form�ł̃p�����[�^��
	private String passwdParamName = "password";
	// ���O�C������hidden�p�����[�^
	private String hiddenNextURL = "next_url=/home.pl";

	// mixi�{�C�X�̃��X�g
	List<mixiVoiceData> voiceList = new ArrayList<mixiVoiceData>();
	// ��͒��Ɍ��ꂽmixi�{�C�X�̂P���e���f�[�^
	mixiVoiceData voiceData;
	// ��͒��Ɍ��ꂽmixi�{�C�X�̂P���e���R�����g�f�[�^
	mixiVoiceData voiceComment;

	/**
	 * @param loginId mixi�Ƀ��O�C������ID
	 * @param password ���O�C���p�X���[�h
	 */
	public mixiVoiceLogService(String loginId, String password) {
		super();
		this.loginId = loginId;
		this.password = password;
	}


	public List<mixiVoiceData> getMixiVoiceLog() {

		// Cookie�̐ݒ�
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);

		String response;

		try {

			/*
			 * mixi�ւ̃��O�C�����s��
			 */
			URL url1 = new URL(loginURL);
			HttpURLConnection loginCon = (HttpURLConnection) url1.openConnection();
			loginCon.setRequestMethod("POST");
			loginCon.setDoOutput(true);
			loginCon.setInstanceFollowRedirects(true);
			OutputStream os = loginCon.getOutputStream();
			//mixi���O�C������POST�f�[�^
			String postData = hiddenNextURL + "&" + loginIdParamName + "=" + loginId + "&" + passwdParamName + "=" + password;
			System.out.println("POST DATA : " + postData);
			PrintStream ps = new PrintStream(os);
			ps.print(postData);
			ps.close();

			//POST���ʂ̎擾
			response = getHttpResponse(loginCon);
			loginCon.disconnect();

			/*
			 * ���O�C�����̃��t���b�V��
			 */
			URL url2 = new URL(refleshURL);
			HttpURLConnection refreshCon = (HttpURLConnection) url2.openConnection();
			refreshCon.setRequestMethod("GET");
			response = getHttpResponse(refreshCon);
			refreshCon.disconnect();

			/*
			 * mixi Voice(Recent�j�̎擾
			 */
			URL url3 = new URL(voiceRecentURL);
			HttpURLConnection voiceCon = (HttpURLConnection) url3.openConnection();
			voiceCon.setRequestMethod("GET");
			response = getHttpResponse(voiceCon);
			voiceCon.disconnect();

			/*
			 * HTML�̉��
			 */
			Parser htmlParser = new Parser(response);
			NodeList htmlList = htmlParser.parse(null);
			//�e�v�f�̃C�e���[�^���擾
			NodeIterator i = htmlList.elements();
			//�e�m�[�h�ɑ΂��鏈�����s��
			while(i.hasMoreNodes()){
				System.out.println("-------��͊J�n---------");
				processNode(i.nextNode());
			}

		} catch (MalformedURLException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		return voiceList;
	}

	/**
	 * �T�[�o����̃��X�|���X���󂯎��
	 * �󂯎�������X�|���X�͕�����Ƃ��ĕԂ�
	 * @param urlCon URL�R�l�N�V����
	 * @return �T�[�o����̃��X�|���X
	 */
	private String getHttpResponse(URLConnection urlCon) {
		InputStream is;

		StringBuffer sf = new StringBuffer();
		try {
			is = urlCon.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"JISAutoDetect"));
			String buffer;
			System.out.println("------------���O�C�����̃��X�|���X------------");
			while ((buffer = reader.readLine()) != null) {
				System.out.println(buffer);
				sf.append(buffer);
			}
			System.out.println("----------------------------------------------");
			reader.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		return sf.toString();
	}

	private void processNode(Node node) throws ParserException {

		// ������̃m�[�h�i�^�O�łȂ��m�[�h�j
		if(node instanceof TextNode) {
			TextNode text = (TextNode) node;
			//System.out.println("TEXT : " + text.getText());
		}
		// �R�����g�̃m�[�h�i<!-- -->�j
		else if (node instanceof RemarkNode) {
			RemarkNode remark = (RemarkNode) node;
			//System.out.println("REMARK : " + remark.getText());
			// �{�C�X�P���e���̏I���������R�����g�������Ă��鎞
			if(remark.getText().equals("/archive")) {
				//���X�g�փf�[�^��ۑ�
				voiceList.add(voiceData);
			}
		}
		// �^�O�̃m�[�h
		else if (node instanceof TagNode) {
			TagNode tag = (TagNode) node;
			String tagName = tag.getTagName();
			//System.out.println("TAG NAME :" + tagName);
			//�{�C�X���e���������class="archive"��T��
			if (tagName.equals("LI")) {
				String classAttribute = tag.getAttribute("class");
				if(classAttribute != null){
					if (classAttribute.equals("archive")){
						voiceData = new mixiVoiceData();
					}
				}
			}
			//�{�C�X�̖{����T��
			else if (tagName.equals("DIV")) {
				String classAttribute = tag.getAttribute("class");
				System.out.println(tag.getAttributesEx().toString());
				if(classAttribute != null){
					if(classAttribute.equals("voiced")) {
						NodeList nl = tag.getChildren();
						//�������{����������
						String voiceNode = nl.toHtml();
						voiceNode = voiceNode.substring(voiceNode.indexOf("<p>")+3, voiceNode.indexOf("<span>"));
						System.out.println("�{���F" + voiceNode);
					}
					//�{�C�X�̓��e�҂̃j�b�N�l�[����T��
					else if(classAttribute.equals("thumbArea")) {
						NodeList nl = tag.getChildren();
						//�������j�b�N�l�[����������
						String voiceNode = nl.toHtml();
						voiceNode = voiceNode.substring(0, voiceNode.lastIndexOf("</a>"));
						voiceNode = voiceNode.substring(voiceNode.lastIndexOf(">")+1, voiceNode.length());
						System.out.println("�j�b�N�l�[���F" + voiceNode);
					}
				}
			}

			//�^�O�͂Ƃ肠�����q�m�[�h�̌������s��
			NodeList nl = tag.getChildren();
			if(nl != null) {
				NodeIterator i = nl.elements();
				while(i.hasMoreNodes()) {
					processNode(i.nextNode());
				}
			}
		}
	}
}
