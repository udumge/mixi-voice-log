/**
 * @author udumbara
 * mixiVoiceLogServiceのテストクラス
 */
public class mixiVoiceLogTest {

	/**
	 * @param args 第一引数はユーザID、第二引数はパスワード
	 */
	public static void main(String[] args) {

		//if( args.length < 2) {
		//	System.out.println("第一引数はユーザID、第二引数はパスワード");
		//	System.exit(1);
		//}

		//mixiVoiceLogService service = new mixiVoiceLogService(args[0], args[1]);
		mixiVoiceLogService service = new mixiVoiceLogService("", "");

		service.getMixiVoiceLog();
	}

}
