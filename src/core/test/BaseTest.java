package core.test;

public abstract class BaseTest implements ITest {

	private int num = 0;
	
	@Override
	public int getTestNum() {
		return num;
	}

	@Override
	public void setTestNum(int i) {
		num = i;
	}

	@Override
	public boolean test() {
		boolean result = true;
		for( int i=0; i < num; i++){
			result = subTest();
			if( !result ){
				break;
			}
		}
		return result;
	}
	
	public abstract boolean subTest();

}
