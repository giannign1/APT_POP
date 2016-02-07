package it.giannign1.pop_api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Target( value = TYPE )
@Retention( RetentionPolicy.SOURCE )
public @interface POPParseObject {
    String[] strings() default { };
    String[] ints() default { };
    String[] booleans() default { };
    String[] dates() default { };
    String[] lists() default { };
    String[] geopoints() default { };
    String[] files() default { };
    String[] parseobjects() default { };
    String[] relations() default { };
    String toStringFormat() default "";
    String[] toStringParameters() default { };
    
    POPsuperClassType superClassType() default POPsuperClassType.ParseObject;
    
    public static enum POPsuperClassType {
    	ParseObject, ParseUser;
    }
}
