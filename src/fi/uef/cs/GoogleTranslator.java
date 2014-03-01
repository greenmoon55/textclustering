package fi.uef.cs;

import com.google.api.detect.Detect;
import com.google.api.detect.DetectResult;
import com.google.api.translate.Language; 
import com.google.api.translate.Translate;  

public class GoogleTranslator {   
	public static void main(String[] args) throws Exception {     
	
//	Translate.setHttpReferrer("http://translate.google.cn");
//	String translatedText = Translate.execute("你好",Language.CHINESE_SIMPLIFIED, Language.ENGLISH); 
//	System.out.println(translatedText);
//	DetectResult detectResult=Detect.execute("you are good");
//	Language language=detectResult.getLanguage();
		
		GoogleTranslator googleTranslator=new GoogleTranslator();
		Translate.setHttpReferrer("http://translate.google.cn");
		String text="你好";
		
		//automatically language dector
		Language language= getDectLanguage(text);
		//translator
		String translateString=getTranslation("食品");
		System.out.println("The orginal language is: "+language.toString());
		System.out.println("The meaning of "+ text +" in English is: "+translateString);

	

	}
	
	// from any language to english
	public static String getTranslation(String text) throws Exception{
		Translate.setHttpReferrer("http://translate.google.cn");
		Language LanguageFrom=GoogleTranslator.getDectLanguage(text);		
		String translationString= Translate.execute(text,LanguageFrom, Language.ENGLISH);
		return translationString; 				
	}
	
	//from finish to english
	public static String getTranslationByLanguage(String text,String language) throws Exception{
		Translate.setHttpReferrer("http://translate.google.cn");
		String translationString=null;
		if(language.trim().equalsIgnoreCase("finish")){
		 translationString= Translate.execute(text,Language.FINNISH, Language.ENGLISH);
		}
		
		return translationString;
	}
	
	//from finish to english
	public static String getTranslationFromEnglishToFinish(String text) throws Exception{
		Translate.setHttpReferrer("http://translate.google.com/#en|fi|");
		String translationString=null;
		
		 translationString= Translate.execute(text,Language.ENGLISH,Language.FINNISH);
		
		return translationString;
	}
	
	public static String getTranslationFromFinishToEnglish(String text) throws Exception{
		Translate.setHttpReferrer("http://translate.google.com/#fi|en|");
		String translationString=null;
		
		 translationString= Translate.execute(text,Language.FINNISH,Language.ENGLISH);
		
		return translationString;
	}
	
	// get the which language the text belong
	public static Language getDectLanguage(String text) throws Exception{
		DetectResult detectResult=Detect.execute(text);
		Language language=detectResult.getLanguage();
		return language;
	}
}


