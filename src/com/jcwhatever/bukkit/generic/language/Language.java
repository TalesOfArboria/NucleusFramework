package com.jcwhatever.bukkit.generic.language;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public enum Language {
    /**
     * Afrikaans
     */
    AF_ZA("af_ZA"),
    /**
     * Arabic
     */
    AR_SA("ar_SA"),
    /**
     * Asturian
     */
    AS_ES("as_ES"),
    /**
     * Bulgarian
     */
    BG_BG("bg_BG"),
    /**
     * Catalan
     */
    CA_ES("ca_ES"),
    /**
     * Occitan
     */
    CC_CT("cc_CT"),
    /**
     * Czech
     */
    CS_CZ("cs_CZ"),
    /**
     * Welsh
     */
    CY_GB("cy_GB"),
    /**
     * Danish
     */
    DA_DK("da_DK"),
    /**
     * German
     */
    DE_DE("de_DE"),
    /**
     * Greek
     */
    EL_GR("el_GR"),
    /**
     * Australian English
     */
    EN_AU("en_AU"),
    /**
     * Canadian English
     */
    EN_CA("en_CA"),
    /**
     * British English
     */
    EN_GB("en_GB"),
    /**
     * Pirate English
     */
    EN_PT("en_PT"),
    /**
     * English US
     */
    EN_US("en_US"),
    /**
     * Esperanto
     */
    EO_UY("eo_UY"),
    /**
     * Argentinean Spanish
     */
    ES_AR("es_AR"),
    /**
     * Spanish
     */
    ES_ES("es_ES"),
    /**
     * Mexico Spanish
     */
    ES_MX("es_MX"),
    /**
     * Uruguayan Spanish
     */
    ES_UY("es_UY"),
    /**
     * Venezuela Spanish
     */
    ES_VE("es_VE"),
    /**
     * Estonian
     */
    ET_EE("et_EE"),
    /**
     * Basque
     */
    EU_ES("eu_ES"),
    /**
     * Finnish
     */
    FI_FI("fi_FI"),
    /**
     * French Canadian
     */
    FR_CA("fr_CA"),
    /**
     * French
     */
    FR_FR("fr_FR"),
    /**
     * Irish
     */
    GA_IE("ga_IE"),
    /**
     * Galician
     */
    GL_ES("gl_ES"),
    /**
     * Hindi
     */
    HI_IN("hi_IN"),
    /**
     * Hebrew / Croatian
     */
    HR_HR("hr_HR"),
    /**
     * Hungarian
     */
    HU_HU("hu_HU"),
    /**
     * Armenian
     */
    HY_AM("hy_AM"),
    /**
     * Indonesian
     */
    ID_ID("id_ID"),
    /**
     * Icelandic
     */
    IS_IS("is_IS"),
    /**
     * Italian
     */
    IT_IT("it_IT"),
    /**
     * Japanese
     */
    JA_JP("ja_JP"),
    /**
     * Georgian
     */
    KA_GE("ka_GE"), 
    /**
     * Korean
     */
    KO_KR("ko_KR"),
    /**
     * Cornwall
     */
    KW_GB("kw_GB"),
    /**
     * Lingua Latina
     */
    LA_LA("la_LA"),
    /**
     * Latin
     */
    LA_VA("la_VA"),
    /**
     * Luxembourgish
     */
    LB_LU("lb_LU"), 
    /**
     * Lithuanian
     */
    LT_LT("lt_LT"), 
    /**
     * Latvian    
     */
    LV_LV("lv_LV"),
    /**
     * Malay
     */
    MS_MY("ms_MY"),
    /**
     * Maltese
     */
    MT_MT("mt_MT"),
    /**
     * Norwegian
     */
    NB_NO("nb_NO"),
    /**
     * Dutch
     */
    NL_NL("nl_NL"),
    /**
     * Norwegian
     */
    NO_NO("no_NO"),
    /**
     * Polish
     */
    PL_PL("pl_PL"),
    /**
     * Brazilian Portuguese
     */
    PT_BR("pt_BR"),
    /**
     * Portuguese
     */
    PT_PT("pt_PT"),
    /**
     * Queneya
     */
    QYA_AA("qya_AA"), 
    /**
     * Romanian
     */
    RO_RO("ro_RO"),
    /**
     * Russuan
     */
    RU_RU("ru_RU"),
    /**
     * Slovak
     */
    SK_SK("sk_SK"),
    /**
     * Slovenian
     */
    SL_SI("sl_SI"),
    /**
     * Serbian
     */
    SR_RS("sr_RS"),
    /**
     * Swedish
     */
    SV_SE("sv_SE"),
    /**
     * Thai
     */
    TH_TH("th_TH"),
    /**
     * Klingon
     */
    TLH_AA("tlh_AA"),
    /**
     * Turkish
     */
    TR_TR("tr_TR"),
    /**
     * Ukranian
     */
    UK_UA("uk_UA"),
    /**
     * Valencian    
     */
    VA_ES("va_ES"),
    /**
     * Vietnamese
     */
    VI_VN("vi_VN"), 
    /**
     * Simplified Chinese
     */
    ZH_CN("zh_CN"),
    /**
     * Traditional Chinese
     */
    ZH_TW("zh_TW");

    
    private static Map<String, Language> _langMap = null;
    private final String _langCode;
    
    Language(String langCode) {
        _langCode = langCode;
    }
    
    public String getLangCode() {
        return _langCode;
    }
    
    @Nullable
    public static Language from(String langCode) {
        buildLangMap();
        
        return _langMap.get(langCode);
    }
    
    private static void buildLangMap() {
        if (_langMap != null)
            return;
        
        _langMap = new HashMap<String, Language>(Language.values().length);
        
        for (Language lang : Language.values()) {
            _langMap.put(lang.getLangCode(), lang);
        }
    }
}
