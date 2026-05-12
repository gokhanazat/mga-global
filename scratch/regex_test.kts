
fun main() {
    val xml = """
<item>
	<title><![CDATA[AB ve ABD, kritik mineraller anlaşmasına yakın]]></title>
	<description><![CDATA[Avrupa Birliği (AB) ve ABD'nin, Çin'e olan bağımlılığı azaltmak için kritik minerallerin üretimi ve güvenliğinin sağlanması konusunda koordinasyon sağlamak üzere anlaşmaya varmaya yaklaştığı bildirildi. ]]></description>
	<pubDate><![CDATA[Fri, 10 Apr 2026 09:00:15 +0000]]></pubDate>
	<image><![CDATA[https://geoim.bloomberght.com/2026/04/10/ver1775811615/3774441_kutu.jpg]]></image>
	<link><![CDATA[https://www.bloomberght.com/ab-ve-abd-kritik-mineraller-anlasmasina-yakin-3774441]]></link>
	<guid><![CDATA[https://www.bloomberght.com/ab-ve-abd-kritik-mineraller-anlasmasina-yakin-3774441]]></guid>
</item>
    """.trimIndent()

    val itemImageRegex = Regex("(?si)<image[^>]*>(?:<!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</image>")
    
    val match = itemImageRegex.find(xml)
    if (match != null) {
        val group1 = match.groupValues[1]
        println("Match found!")
        println("Group 1: '$group1'")
    } else {
        println("No match found")
    }
}
main()
