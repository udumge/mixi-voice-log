/**
 * @author udumbara
 * mixiVoiceLogService�̃e�X�g�N���X
 */
public class mixiVoiceLogTest {

	/**
	 * @param args �������̓��[�UID�A�������̓p�X���[�h
	 */
	public static void main(String[] args) {

		//if( args.length < 2) {
		//	System.out.println("�������̓��[�UID�A�������̓p�X���[�h");
		//	System.exit(1);
		//}

		//mixiVoiceLogService service = new mixiVoiceLogService(args[0], args[1]);
		mixiVoiceLogService service = new mixiVoiceLogService("", "");

		service.getMixiVoiceLog();
	}

}
