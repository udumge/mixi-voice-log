import java.util.List;

/**
 * @author udumge
 * mixi�{�C�X�̃f�[�^��ێ�����N���X(2010/5/9�j
 */
public class mixiVoiceData {

	// �����������[�U�̃j�b�N�l�[��
	private String userId;

	// ����
	private String voice;

	//�����ɑ΂���R�����g
	private List<mixiVoiceData> comment;

	/**
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId �Z�b�g���� userId
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
	 * @param voice �Z�b�g���� voice
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
	 * @param comment �Z�b�g���� comment
	 */
	public void setComment(List<mixiVoiceData> comment) {
		this.comment = comment;
	}

}
