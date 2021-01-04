package itopvpn.free.vpn.proxy.utils
import java.util.regex.Pattern

object StringUtils {

    private val weakEmailPattern:Pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")

    val strongEmailPattern:Pattern = Pattern.compile("^([A-Za-z0-9_\\-.+])+@([A-Za-z0-9_\\-.+])+\\.([A-Za-z]{2,14})\$")

    fun isEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            weakEmailPattern.matcher(email).matches()
        }
    }

    fun isEmail(email: String,pattern:Pattern): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            pattern.matcher(email).matches()
        }
    }

    fun formatString2(text:Any):String = String.format("%.2f", text)

    fun formatString(text:String,n: Int):String {
        if(text.isEmpty()){
            return text
        }
        val list = text.split(".")
        if(list.size != 2){
            return text
        }
        if(list[1].length < 2){
            return text
        }
        return list[0]+"."+list[1].substring(0,n)
    }


    fun formatString1(text:Any):String = String.format("%.1f", text)
}
