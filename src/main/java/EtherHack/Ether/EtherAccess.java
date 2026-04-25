package EtherHack.Ether;
  
import java.lang.Object;
import java.lang.reflect.Field;

public class EtherAccess
{
    private static EtherAccess instance;
    private Field field;
  
    public void use(Class<?> clazz, String fieldName, Object value)
    {
      try
      {
        field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);  // or your 'accessible' value
        field.set(null, value);
      }
      catch (Exception e) 
      {
        e.printStackTrace();
      }
    }

    public static EtherAccess getInstance()
    {
        if (instance == null)
          instance = new EtherAccess();
      
        return instance;
    }
}
