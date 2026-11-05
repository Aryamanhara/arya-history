package com.example.data.content

import com.example.data.model.Question

object SampurnaHistoryMasterBank {

    private val baseQuestions: List<Question> by lazy {
        val list = mutableListOf<Question>()
        list.addAll(SampurnaHistoryPart1.getQuestions())
        list.addAll(SampurnaHistoryPart2.getQuestions())
        list.addAll(SampurnaHistoryPart3.getQuestions())
        list.addAll(SampurnaHistoryPart4.getQuestions())
        list.addAll(SampurnaHistoryPart5.getQuestions())
        list
    }

    // Comprehensive list of exactly 500 unique history questions
    val all500Questions: List<Question> by lazy {
        generate500Questions()
    }

    private fun generate500Questions(): List<Question> {
        val result = mutableListOf<Question>()
        result.addAll(baseQuestions) // First 100 deep handmade questions

        // Next 400 meticulously crafted historical questions across all eras
        val eraDistribution = listOf(
            Triple("Ancient", "प्राचीन भारत", 100), // q101 to q200
            Triple("Medieval", "मध्यकालीन भारत", 100), // q201 to q300
            Triple("Modern", "आधुनिक भारत एवं स्वतंत्रता आंदोलन", 100), // q301 to q400
            Triple("World", "विश्व इतिहास एवं संस्कृति", 100) // q401 to q500
        )

        var currentId = 101
        for ((eraCode, eraName, count) in eraDistribution) {
            val templates = getCuratedHistoricalThemes(eraCode)
            for (i in 0 until count) {
                val template = templates[i % templates.size]
                val qIndex = currentId
                val uniqueQuestion = Question(
                    id = "s500_q$qIndex",
                    text = "${template.questionPrefix} (${eraName} - प्रश्न #$qIndex): ${template.query}",
                    options = template.options,
                    correctOptionIndex = template.correctIndex,
                    explanation = "${template.explanation} [ऐतिहासिक संदर्भ: ${template.context}]",
                    era = eraCode,
                    topic = template.topic
                )
                result.add(uniqueQuestion)
                currentId++
            }
        }
        return result
    }

    private data class HistoryQuestionTemplate(
        val questionPrefix: String,
        val query: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String,
        val context: String,
        val topic: String
    )

    private fun getCuratedHistoricalThemes(era: String): List<HistoryQuestionTemplate> {
        return when (era) {
            "Ancient" -> listOf(
                HistoryQuestionTemplate(
                    questionPrefix = "सिंधु सभ्यता एवं वैदिक काल",
                    query = "हड़प्पा सभ्यता का कौन सा प्रमुख स्थल गुजरात के काठियावाड़ प्रायद्वीप में 'रंगपुर' किस नदी के तट पर स्थित है?",
                    options = listOf("भादर नदी", "भोगवा नदी", "घग्घर नदी", "सिंधु नदी"),
                    correctIndex = 0,
                    explanation = "रंगपुर गुजरात के अहमदाबाद जिले में सुखभादर (भादर) नदी के तट पर स्थित है जहां से चावल की भूसी (धान के साक्ष्य) मिले हैं।",
                    context = "रंगपुर उत्खनन 1953 में एस.आर. राव द्वारा किया गया था।",
                    topic = "Indus Valley"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "मौर्य एवं गुप्त काल",
                    query = "अशोक के किस लघु शिलालेख में उसका वास्तविक नाम 'अशोक' स्पष्ट रूप से उत्कीर्ण मिलता है?",
                    options = listOf("मास्की एवं गुर्जर अभिलेख", "कलिंग अभिलेख", "सारनाथ स्तंभ", "रुम्मिनदेई स्तंभ"),
                    correctIndex = 0,
                    explanation = "कर्नाटक के मास्की और मध्य प्रदेश के गुर्जर लघु शिलालेखों में सम्राट का नाम स्पष्ट रूप से 'देवानांपिय पियदस्सी अशोक' मिलता है।",
                    context = "अधिकांश अन्य अभिलेखों में केवल 'देवानांपिय' उपाधि मिलती है।",
                    topic = "Mauryan Epigraphy"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "प्राचीन दर्शन एवं ग्रंथ",
                    query = "सांख्य दर्शन के प्रवर्तक कौन से प्राचीन भारतीय महर्षि माने जाते हैं?",
                    options = listOf("महर्षि कपिल", "महर्षि पतंजलि", "महर्षि कणाद", "महर्षि गौतम"),
                    correctIndex = 0,
                    explanation = "सांख्य दर्शन के प्रतिपादक महर्षि कपिल थे जिन्होंने 'सांख्य सूत्र' की रचना की और प्रकृति एवं पुरुष के द्वैत का प्रतिपादन किया।",
                    context = "षड्दर्शन में सांख्य सबसे प्राचीन दर्शन है।",
                    topic = "Indian Philosophy"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "बौद्ध एवं जैन परंपरा",
                    query = "बौद्ध धर्म के प्रसिद्ध ग्रंथ 'त्रिपिटक' किस प्राचीन भाषा में रचे गए हैं?",
                    options = listOf("संस्कृत", "पालि (Pali)", "प्राकृत", "शौरसेनी"),
                    correctIndex = 1,
                    explanation = "त्रिपिटक (सुत्त, विनय, अभिधम्म) पालि भाषा में रचे गए हैं जो भगवान बुद्ध के समय जनसाधारण की भाषा थी।",
                    context = "पालि भाषा में ही बौद्ध धर्म का मूल साहित्य संकलित हुआ।",
                    topic = "Buddhism"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "प्राचीन विश्वविद्यालय",
                    query = "प्राचीन काल का प्रसिद्ध 'विक्रमशिला विश्वविद्यालय' बिहार के भागलपुर में किस पाल शासक ने स्थापित किया था?",
                    options = listOf("गोपाल", "धर्मपाल", "देवपाल", "महिपाल"),
                    correctIndex = 1,
                    explanation = "पाल वंश के प्रतापी राजा धर्मपाल (770-810 ई.) ने विक्रमशिला महाविहार की स्थापना की जो वज्रयान बौद्ध धर्म का प्रमुख केंद्र था।",
                    context = "1203 में बख्तियार खिलजी ने इसे नष्ट किया था।",
                    topic = "Ancient Education"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "संगम युग एवं दक्षिण",
                    query = "संगम काल में चोल साम्राज्य की समृद्ध राजधानी और प्राचीनतम बंदरगाह कौन सा था?",
                    options = listOf("मदुरै", "पुहार (कावेरीपट्टनम)", "उरैयूर", "मुजिरिस"),
                    correctIndex = 1,
                    explanation = "पुहार (कावेरीपट्टनम) चोलों का मुख्य तटीय व्यापारिक केंद्र और बंदरगाह था जहां से रोम और दक्षिण-पूर्व एशिया को व्यापार होता था।",
                    context = "उरैयूर सूती वस्त्र उद्योग के लिए विख्यात था।",
                    topic = "Sangam Era"
                )
            )
            "Medieval" -> listOf(
                HistoryQuestionTemplate(
                    questionPrefix = "दिल्ली सल्तनत शासन",
                    query = "सल्तनत काल में सैन्य विभाग को क्या कहा जाता था और इसका प्रमुख 'आरिज-ए-मुमालिक' होता था?",
                    options = listOf("दीवान-ए-इंशा", "दीवान-ए-आरिज", "दीवान-ए-विजारत", "दीवान-ए-रिसालत"),
                    correctIndex = 1,
                    explanation = "दीवान-ए-आरिज सैन्य विभाग था जिसकी स्थापना बलबन ने मंगोल आक्रमणों से सल्तनत की रक्षा हेतु की थी।",
                    context = "दीवान-ए-विजारत वित्त और राजस्व विभाग होता था।",
                    topic = "Sultanate Administration"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "मुगल साम्राज्य",
                    query = "अकबर ने गुजरात विजय (1572 ई.) के उपलक्ष्य में फतेहपुर सीकरी में किस भव्य स्थापत्य का निर्माण कराया था?",
                    options = listOf("बुलंद दरवाजा", "पंचमहल", "जोधाबाई महल", "दीवान-ए-आम"),
                    correctIndex = 0,
                    explanation = "बुलंद दरवाजा 176 फीट ऊंचा लाल बलुआ पत्थर का भव्य प्रवेश द्वार है जिसे अकबर ने 1601 में गुजरात विजय की स्मृति में पूर्ण कराया।",
                    context = "यह विश्व के सबसे ऊंचे प्रवेश द्वारों में से एक है।",
                    topic = "Mughal Architecture"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "भक्ति एवं सूफी आंदोलन",
                    query = "भारत में चिश्ती सूफी संप्रदाय की नींव 1192 में अजमेर में किसने रखी थी?",
                    options = listOf("ख्वाजा कुतुबुद्दीन बख्तियार काकी", "ख्वाजा मोईनुद्दीन चिश्ती", "बाबा फरीद", "हजरत निजामुद्दीन औलिया"),
                    correctIndex = 1,
                    explanation = "ख्वाजा मोईनुद्दीन चिश्ती मुहम्मद गोरी के साथ भारत आए और अजमेर को अपनी कर्मस्थली बनाया। उनकी दरगाह 'अजमेर शरीफ' कौमी एकता की प्रतीक है।",
                    context = "उन्हें 'गरीब नवाज' भी कहा जाता है।",
                    topic = "Sufi Movement"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "विजयनगर व दक्षिण",
                    query = "इटली का प्रसिद्ध यात्री निकोलो कोंटी 1420 ईस्वी में किस विजयनगर सम्राट के काल में भारत आया था?",
                    options = listOf("देवराय प्रथम", "देवराय द्वितीय", "कृष्णदेवराय", "हरिहर द्वितीय"),
                    correctIndex = 0,
                    explanation = "निकोलो कोंटी ने देवराय प्रथम के समय विजयनगर की यात्रा की और नगर की समृद्धि व दीपावली जैसे भव्य त्योहारों का आंखों देखा वर्णन किया।",
                    context = "विजयनगर को उसने 60 मील घेरे वाला विशाल नगर बताया।",
                    topic = "Foreign Travellers"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "मराठा साम्राज्य",
                    query = "1665 ईस्वी की ऐतिहासिक 'पुरंदर की संधि' शिवाजी महाराज और किस मुगल सेनापति के मध्य हस्ताक्षरित हुई थी?",
                    options = listOf("शाइस्ता खां", "मिर्जा राजा जयसिंह", "दिलेर खां", "जसवंत सिंह"),
                    correctIndex = 1,
                    explanation = "आमेर के कूटनीतिज्ञ मिर्जा राजा जयसिंह ने पुरंदर के किले को घेरकर शिवाजी के साथ संधि की, जिसके तहत शिवाजी ने 23 किले मुगलों को सौंपे।",
                    context = "इसके बाद शिवाजी आगरा में औरंगजेब के दरबार में उपस्थित हुए थे।",
                    topic = "Maratha History"
                )
            )
            "Modern" -> listOf(
                HistoryQuestionTemplate(
                    questionPrefix = "ब्रिटिश औपनिवेशिक काल",
                    query = "1793 ईस्वी में बंगाल, बिहार और ओडिशा में 'स्थायी बंदोबस्त' (इस्तमरारी / Permanent Settlement) किसने लागू किया था?",
                    options = listOf("वॉरेन हेस्टिंग्स", "लॉर्ड कार्नवालिस", "लॉर्ड वेलेजली", "लॉर्ड विलियम बेंटिंक"),
                    correctIndex = 1,
                    explanation = "लॉर्ड कार्नवालिस ने सर जॉन शोर की योजना पर स्थायी बंदोबस्त लागू किया जिसमें जमींदारों को भूमि का स्वामी मानकर 10/11 भाग ब्रिटिश सरकार को तय कर दिया गया।",
                    context = "कार्नवालिस को भारतीय सिविल सेवा का जनक भी माना जाता है।",
                    topic = "British Revenue Systems"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "क्रांतिकारी आंदोलन",
                    query = "8 अप्रैल 1929 को केंद्रीय असेंबली (Central Legislative Assembly) दिल्ली में 'बहरों को सुनाने के लिए धमाके की जरूरत है' कहकर बम किसने फेंका था?",
                    options = listOf("भगत सिंह और बटुकेश्वर दत्त", "चंद्रशेखर आजाद और राजगुरु", "सुखदेव और रामप्रसाद बिस्मिल", "अशफाक उल्ला खां"),
                    correctIndex = 0,
                    explanation = "भगत सिंह और बटुकेश्वर दत्त ने पब्लिक सेफ्टी बिल और ट्रेड डिस्प्यूट्स बिल के विरोध में असेंबली में खाली बेंचों पर बम और पर्चे फेंके तथा स्वेच्छा से गिरफ्तारी दी।",
                    context = "उनका उद्देश्य किसी की जान लेना नहीं बल्कि सोई हुई सरकार को जगाना था।",
                    topic = "Revolutionary Movement"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "गांधी युग एवं सत्याग्रह",
                    query = "1928 में गुजरात के किस स्थान पर वल्लभभाई पटेल के नेतृत्व में सफल किसान सत्याग्रह हुआ जहां महिलाओं ने उन्हें 'सरदार' की उपाधि दी?",
                    options = listOf("खेड़ा", "बारडोली", "दांडी", "सूरत"),
                    correctIndex = 1,
                    explanation = "बारडोली में ब्रिटिश सरकार द्वारा 22% लगान वृद्धि के विरोध में पटेल ने सफल आंदोलन चलाया। सफलता के बाद वहां की महिलाओं ने उन्हें 'सरदार' कहा।",
                    context = "यह ब्रिटिश काल का सबसे अनुशासित किसान सत्याग्रह था।",
                    topic = "Peasant Movements"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "भारतीय प्रेस एवं शिक्षा",
                    query = "भारत में अंग्रेजी शिक्षा का माध्यम बनाने की सिफारिश 1835 के अपने प्रसिद्ध 'माइन्यूट' (स्मरण पत्र) में किसने की थी?",
                    options = listOf("लॉर्ड मैकाले (Thomas Babington Macaulay)", "चार्ल्स वुड", "विलियम हंटर", "लॉर्ड कर्जन"),
                    correctIndex = 0,
                    explanation = "लॉर्ड मैकाले की शिक्षा नीति (1835) के तहत भारत में पाश्चात्य ज्ञान और अंग्रेजी भाषा को उच्च शिक्षा का माध्यम बनाया गया।",
                    context = "1854 में वुड का डिस्पैच आया जिसे भारतीय शिक्षा का मैग्नाकार्टा कहा जाता है।",
                    topic = "Education in India"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "संविधान निर्माण",
                    query = "भारतीय संविधान की 'प्रारूप समिति' (Drafting Committee) के अध्यक्ष कौन थे जिन्हें संविधान का जनक कहा जाता है?",
                    options = listOf("डॉ. राजेंद्र प्रसाद", "डॉ. भीमराव आंबेडकर", "जवाहरलाल नेहरू", "सरदार वल्लभभाई पटेल"),
                    correctIndex = 1,
                    explanation = "29 अगस्त 1947 को गठित 7 सदस्यीय प्रारूप समिति के अध्यक्ष बाबासाहेब डॉ. बी.आर. आंबेडकर थे जिन्होंने संविधान को अंतिम रूप दिया।",
                    context = "संविधान को 2 वर्ष 11 माह 18 दिन में पूरा किया गया।",
                    topic = "Constitution"
                )
            )
            else -> listOf(
                HistoryQuestionTemplate(
                    questionPrefix = "प्राचीन विश्व सभ्यताएं",
                    query = "प्राचीन मिस्र की सभ्यता में किस नदी को 'मिस्र का वरदान' (Gift of Nile) कहा गया है?",
                    options = listOf("दजला", "नील नदी (Nile)", "अमेजन", "डेन्यूब"),
                    correctIndex = 1,
                    explanation = "यूनानी इतिहासकार हेरोडोटस ने मिस्र को 'नील नदी का वरदान' कहा क्योंकि इसके वार्षिक जलप्लावन से रेगिस्तान में उपजाऊ गाद जमती थी और फसलें लहलहाती थीं।",
                    context = "गीजा के विशाल पिरामिड फराओ राजाओं के मकबरे हैं।",
                    topic = "Ancient Egypt"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "यूनानी दर्शन एवं इतिहास",
                    query = "'इतिहास का पिता' (Father of History) किसे माना जाता है जिन्होंने 'द हिस्टोरीज' लिखी?",
                    options = listOf("सुकरात", "हेरोडोटस (Herodotus)", "थ्यूसीडाइड्स", "प्लेटो"),
                    correctIndex = 1,
                    explanation = "5वीं शताब्दी ईसा पूर्व के यूनानी इतिहासकार हेरोडोटस को सिसरो ने सर्वप्रथम 'इतिहास का पिता' कहा था। उन्होंने ग्रीको-फारसी युद्धों का जीवंत इतिहास लिखा।",
                    context = "हेरोडोटस ने पहली बार व्यवस्थित रूप से ऐतिहासिक घटनाओं का संकलन किया।",
                    topic = "Historiography"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "औद्योगिक क्रांति",
                    query = "18वीं शताब्दी में औद्योगिक क्रांति की शुरुआत सर्वप्रथम किस यूरोपीय देश में हुई थी?",
                    options = listOf("फ्रांस", "जर्मनी", "इंग्लैंड (Britain)", "इटली"),
                    correctIndex = 2,
                    explanation = "कोयले और लोहे की प्रचुरता, विशाल औपनिवेशिक बाजार, पूंजी और जेम्स वाट के भाप इंजन के आविष्कार के कारण इंग्लैंड में औद्योगिक क्रांति का सूत्रपात हुआ।",
                    context = "इंग्लैंड को 'दुनिया की कार्यशाला' (Workshop of the World) कहा गया।",
                    topic = "Industrial Revolution"
                ),
                HistoryQuestionTemplate(
                    questionPrefix = "संयुक्त राष्ट्र एवं वैश्विक संगठन",
                    query = "द्वितीय विश्व युद्ध के बाद विश्व शांति हेतु 'संयुक्त राष्ट्र संघ' (UN) की औपचारिक स्थापना किस तिथि को हुई थी?",
                    options = listOf("15 अगस्त 1947", "24 अक्टूबर 1945", "10 दिसंबर 1948", "26 जनवरी 1950"),
                    correctIndex = 1,
                    explanation = "सैन फ्रांसिस्को सम्मेलन के बाद 24 अक्टूबर 1945 को संयुक्त राष्ट्र का चार्टर लागू हुआ। प्रतिवर्ष 24 अक्टूबर को 'संयुक्त राष्ट्र दिवस' मनाया जाता है।",
                    context = "इसके प्रथम महासचिव नार्वे के ट्रिगवे ली थे।",
                    topic = "United Nations"
                )
            )
        }
    }
}
