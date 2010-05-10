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
 * mixiボイスを取得するサービス
 */
public class mixiVoiceLogService {

	//mixiのログインURL
	private String loginURL = "http://mixi.jp/login.pl?from=login1";

	//mixiログイン時のリフレッシュ（なにこれ？）
	private String refleshURL = "http://mixi.jp/check.pl?n=%2Fhome.pl";

	//mixiのボイスURL
	private String voiceRecentURL = "http://mixi.jp/recent_voice.pl";

	private String loginId;
	private String password;

	// ログインIDのFormでのパラメータ名
	private String loginIdParamName = "email";
	// パスワードのFormでのパラメータ名
	private String passwdParamName = "password";
	// ログイン時のhiddenパラメータ
	private String hiddenNextURL = "next_url=/home.pl";

	// mixiボイスのリスト
	List<mixiVoiceData> voiceList = new ArrayList<mixiVoiceData>();
	// 解析中に現れたmixiボイスの１投稿分データ
	mixiVoiceData voiceData;
	// 解析中に現れたmixiボイスの１投稿分コメントデータ
	mixiVoiceData voiceComment;

	/**
	 * @param loginId mixiにログインするID
	 * @param password ログインパスワード
	 */
	public mixiVoiceLogService(String loginId, String password) {
		super();
		this.loginId = loginId;
		this.password = password;
	}


	public List<mixiVoiceData> getMixiVoiceLog() {

		// Cookieの設定
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);

		String response;

		try {

			/*
			 * mixiへのログインを行う
			 */
			URL url1 = new URL(loginURL);
			HttpURLConnection loginCon = (HttpURLConnection) url1.openConnection();
			loginCon.setRequestMethod("POST");
			loginCon.setDoOutput(true);
			loginCon.setInstanceFollowRedirects(true);
			OutputStream os = loginCon.getOutputStream();
			//mixiログイン時のPOSTデータ
			String postData = hiddenNextURL + "&" + loginIdParamName + "=" + loginId + "&" + passwdParamName + "=" + password;
			System.out.println("POST DATA : " + postData);
			PrintStream ps = new PrintStream(os);
			ps.print(postData);
			ps.close();

			//POST結果の取得
			response = getHttpResponse(loginCon);
			loginCon.disconnect();

			/*
			 * ログイン時のリフレッシュ
			 */
			URL url2 = new URL(refleshURL);
			HttpURLConnection refreshCon = (HttpURLConnection) url2.openConnection();
			refreshCon.setRequestMethod("GET");
			response = getHttpResponse(refreshCon);
			refreshCon.disconnect();

			/*
			 * mixi Voice(Recent）の取得
			 */
			URL url3 = new URL(voiceRecentURL);
			HttpURLConnection voiceCon = (HttpURLConnection) url3.openConnection();
			voiceCon.setRequestMethod("GET");
			response = getHttpResponse(voiceCon);
			voiceCon.disconnect();

			/*
			 * HTMLの解析
			 */
			Parser htmlParser = new Parser(response);
			NodeList htmlList = htmlParser.parse(null);
			//各要素のイテレータを取得
			NodeIterator i = htmlList.elements();
			//各ノードに対する処理を行う
			while(i.hasMoreNodes()){
				System.out.println("-------解析開始---------");
				processNode(i.nextNode());
			}

		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return voiceList;
	}

	/**
	 * サーバからのレスポンスを受け取る
	 * 受け取ったレスポンスは文字列として返す
	 * @param urlCon URLコネクション
	 * @return サーバからのレスポンス
	 */
	private String getHttpResponse(URLConnection urlCon) {
		InputStream is;

		StringBuffer sf = new StringBuffer();
		try {
			is = urlCon.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"JISAutoDetect"));
			String buffer;
			System.out.println("------------ログイン時のレスポンス------------");
			while ((buffer = reader.readLine()) != null) {
				System.out.println(buffer);
				sf.append(buffer);
			}
			System.out.println("----------------------------------------------");
			reader.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return sf.toString();
	}

	private void processNode(Node node) throws ParserException {

		// 文字列のノード（タグでないノード）
		if(node instanceof TextNode) {
			TextNode text = (TextNode) node;
			//System.out.println("TEXT : " + text.getText());
		}
		// コメントのノード（<!-- -->）
		else if (node instanceof RemarkNode) {
			RemarkNode remark = (RemarkNode) node;
			//System.out.println("REMARK : " + remark.getText());
			// ボイス１投稿分の終了を示すコメントが入っている時
			if(remark.getText().equals("/archive")) {
				//リストへデータを保存
				voiceList.add(voiceData);
			}
		}
		// タグのノード
		else if (node instanceof TagNode) {
			TagNode tag = (TagNode) node;
			String tagName = tag.getTagName();
			//System.out.println("TAG NAME :" + tagName);
			//ボイス投稿一つ分を示すclass="archive"を探す
			if (tagName.equals("LI")) {
				String classAttribute = tag.getAttribute("class");
				if(classAttribute != null){
					if (classAttribute.equals("archive")){
						voiceData = new mixiVoiceData();
					}
				}
			}
			//ボイスの本文を探す
			else if (tagName.equals("DIV")) {
				String classAttribute = tag.getAttribute("class");
				System.out.println(tag.getAttributesEx().toString());
				if(classAttribute != null){
					if(classAttribute.equals("voiced")) {
						NodeList nl = tag.getChildren();
						//無理やり本文だけ抜く
						String voiceNode = nl.toHtml();
						voiceNode = voiceNode.substring(voiceNode.indexOf("<p>")+3, voiceNode.indexOf("<span>"));
						System.out.println("本文：" + voiceNode);
					}
					//ボイスの投稿者のニックネームを探す
					else if(classAttribute.equals("thumbArea")) {
						NodeList nl = tag.getChildren();
						//無理やりニックネームだけ抜く
						String voiceNode = nl.toHtml();
						voiceNode = voiceNode.substring(0, voiceNode.lastIndexOf("</a>"));
						voiceNode = voiceNode.substring(voiceNode.lastIndexOf(">")+1, voiceNode.length());
						System.out.println("ニックネーム：" + voiceNode);
					}
				}
			}

			//タグはとりあえず子ノードの検索を行う
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
