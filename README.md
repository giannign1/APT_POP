# APT_POP
This is an APT Tool for generating subclasses for User-defined ParseObject (in [Parse.com](https://parse.com/) environment)

Normally you have get a ParseObject and access his field by calling `.getString("fieldName")`, `.getInt("fieldName")` and `.put("fieldName", value)`.
This means remember the correct name of the various field and never miss a character or it doesn't work

This **APT Tool** (Annotation Processing Tool) will generate a class with methods for every field of the object

#An example:
```java
@POPParseObject(strings={ "field1", "field2"} )
@ParseClassName("ObjectCustomName")
public class ObjectCustomName { }
```

-Performing a build, APT_POP will generate the following class under build classes:
```java
@SuppressWarnings("unused")
public abstract class ObjectCustomName_POPgen extends ParseObject {
	public static final String POPkey_field1 = "field1";
	public static final String POPkey_field2 = "field2";
	public String POPget_field1() {
		return getString("field1");
	}
	public void POPset_field1(String value) {
		put("field1", value);
	}
	public String POPget_field2() {
		return getString("field2");
	}
	public void POPset_field2(String value) {
		put("field2", value);
	}
}
```

-Now make your class extends this generated class like this:
```java
public class ObjectCustomName extends ObjectCustomName_POPgen {
```

-So now if you have a **ObjectCustomName** instance will be easier getting the fields values:
```java
ObjectCustomName obj = //an instance obtained by a ParseQuery<ObjectCustomName> for example
obj.put("field1", "nuovoValore");
String val = obj.getString("field1");
```
```java
ObjectCustomName obj = //an instance obtained by a ParseQuery<ObjectCustomName> for example
obj.POPset_field1("nuovoValore");
String val = obj.POPget_field1();
```

-Remember declaring the child class to Parse before calling *Parse.initialize(Context)*
```java
ParseObject.registerSubclass(ObjectCustomName.class);
Parse.initialize((Context)this);
```

#Integration
All you need are the two .jar files
```
APT_POP.jar
APT_POP-api.jar
```
Place them in **libs** folder of your project and add the following dependencies to your project *build.gradle*
```
apt files('libs/APT_POP.jar');
compile files('libs/APT_POP-api.jar')
apt 'com.squareup:javapoet:1.3.0'
compile 'com.parse:parse-android:1.10.3' //You surely already have this line
```
Remember, to generate the classes you must perform a **build** of your project and only after that you can set the generated class as superclass of your annotated class

#Usage
First of all you must annotate your class with the *@POPParseObject* annotation:
```java
@POPParseObject
@ParseClassName("ObjectCustomName")//this line is needed by Parse SDK not from my tool
public class ObjectCustomName
```
This annotation can have none, one or many parameters for example:
```java
@POPParseObject(
	strings={ "stringField1", "stringField2"	},
	ints={		"intField1", "intField2"	},
	booleans={"booleanField1", "booleanField2"}
```
##Superclass (ParseObject or ParseUser)
I only talked about *ParseObject* subclasses but we can generate *ParseUser* subclasses too, using the parameter *'superClassType'*:
```java
superClassType = POPParseObject.POPsuperClassType.ParseObject
//Make the generated class extends com.parse.ParseObject
//This is the default superclass, so this line is not necessary for this value
```
```java
superClassType = POPParseObject.POPsuperClassType.ParseUser
//Make the generated class extends com.parse.ParseUser
```
For ParseUser subclassing, the following method will be generated too:
```java
public ParseObject POPasParseObject() {
	return this;
}
```
##ToString method generation
We can make the tool autogenerate, or better, auto-override, the *toString()* method passing a StringFormat and an array of field names as parameters for the StringFormat
Example:
```java
toStringFormat = "ObjectId is %1$s - StringField1 is %2$s",
toStringParameters = { "objectId", "stringField1" }
```
will generate:
```java
@Override
public String toString() {
	try {
		return String.format("ObjectId is %1$s - StringField1 is %2$s", getObjectId(), POPget_stringField1());
	} catch (Exception ignored) {
		return "POP error while computing toString formatting";
	}
}
```
##Fields
Every field has his own type and there is a parameter for each of them.
For every field will be generated a String constant containing the name of field, to be easily used in ParseQuery "where*" methods
For example, for a field of any type with name "value" will be generated:
```java
public static final String POPkey_value = "value";
```
Also will be generated constants for the default ParseObject fields: objectId, createdAt and updatedAt
```java
public static final String POPkey_objectId = "objectId";
public static final String POPkey_createdAt = "createdAt";
public static final String POPkey_updatedAt = "updatedAt";
```
#####String
```java
strings={"value"}
```
```java
public String POPget_value() {
	return getString("value");
}
public void POPset_value(String value) {
	put("value", value);
}
```
#####int
```java
ints={"value"}
```
```java
public String POPget_value() {
	return getInt("value");
}
public void POPset_value(int value) {
	put("value", value);
}
```
#####boolean
```java
booleans={"value"}
```
```java
public boolean POPis_value() {
	return getBoolean("value");
}
public void POPset_value(boolean value) {
	put("value", value);
}
```
#####List<String>
```java
stringArrays={"value"}
```
```java
public List<String> POPget_value() {
	return this.<String>getList("value");
}
public void POPset_value(List<String> value) {
	put("value", value);
}
```
#####Date
```java
dates={"value"}
```
```java
public Date POPget_value() {
	return (Date)this.get("value");
}
public void POPset_value(Date value) {
	put("value", value);
}
```
#####ParseGeoPoint
```java
geopoints={"value"}
```
```java
public ParseGeoPoint POPget_value() {
	return this.getParseGeoPoint("value");
}
public void POPset_value(ParseGeoPoint value) {
	put("value", value);
}
```
#####ParseFile
```java
files={"value"}
```
```java
public ParseFile POPget_value() {
	return this.getParseFile("value");
}
public void POPset_value(ParseFile value) {
	put("value", value);
}
```
#####ParseObject or <? extends ParseObject>
```java
parseobjects={"value"}
```
```java
public <T extends ParseObject> T POPget_value() {
	return (T)this.getParseObject("value");
}
public void POPset_Fatturazione(ParseObject value) {
	put("value", value);
}
```
#####ParseRelation or ParseRelation<? extends ParseObject>
```java
relations={"value"}
```
```java
public <T extends ParseObject> ParseRelation<T> POPget_value() {
	return this.<T>getRelation("value");
}
```






