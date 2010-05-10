import java.util.List;

/**
 * @author udumge
 * mixiボイスのデータを保持するクラス(2010/5/9）
 */
public class mixiVoiceData {

	// 発言したユーザのニックネーム
	private String userId;

	// 発言
	private String voice;

	//発言に対するコメント
	private List<mixiVoiceData> comment;

	/**
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId セットする userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return voice
	 */
	public String getVoice() {
		return voice;
	}
	/**
	 * @param voice セットする voice
	 */
	public void setVoice(String voice) {
		this.voice = voice;
	}
	/**
	 * @return comment
	 */
	public List<mixiVoiceData> getComment() {
		return comment;
	}
	/**
	 * @param comment セットする comment
	 */
	public void setComment(List<mixiVoiceData> comment) {
		this.comment = comment;
	}

}
