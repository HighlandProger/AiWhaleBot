package ru.rusguardian.constant.ai;

import lombok.Getter;

@Getter
public enum AssistantRole {

    USUAL("Обычный \uD83D\uDD01", "", "Эта роль представляет собой стандартное поведение бота, отвечающего на ваши запросы без каких-либо специальных модификаций или ограничений."),
    GOPNIK("Гопник \uD83E\uDDE2", ""),
    REKLAMODATEL("Рекламодатель \uD83D\uDCE2", ""),
    RAPER("Рэпер \uD83C\uDFA4"),
    PEREVODCHIK_SMAILIKOV("Переводчик смайликов \uD83D\uDE03"),
    HACKNUTYJ("DAN(Хакнутый GPT) \uD83D\uDE08", ""),
    ANTI_PLAGIAT("Анти-плагиат \uD83D\uDEAB", ""),
    SCENARIST("Сценарист \uD83D\uDCDD"),
    HISTORIC("Историк \uD83D\uDCDA"),
    FULLSTACK_RAZRABOTCHIK("Fullstack разработчик \uD83D\uDCBB"),
    DEISTVUET_KAK_SEO("Действует как CEO \uD83D\uDC68\u200D\uD83D\uDCBC"),
    MATEMATIC("Математик ➗"),
    FINANSOVIJ_ANALITIC("Финансовый аналитик \uD83D\uDCBC"),
    TERMINAL_LINUX("Терминал Linux \uD83D\uDCBB"),
    INTERPRETATOR_PYTHON("Интерпретатор Python \uD83D\uDC0D"),
    V_DVUH_SLOVAH("В двух словах \uD83D\uDDE3"),
    PSYHOLOG("Психолог \uD83E\uDDE0"),
    MOTIVACIONNYJ_SPIKER("Мотивационный спикер \uD83C\uDF99"),
    TERMINAL_SQL("Терминал SQL\uD83D\uDCCA"),
    INTERPRETATOR_PHP("Интерпретатор PHP\uD83E\uDDD1\u200D\uD83D\uDCBB"),
    SENIOR_FRONTEND_DEVELOPER("Senior Frontend Developer \uD83E\uDDD1\u200D\uD83D\uDCBB"),
    SVG_DESIGNER("SVG дизайнер\uD83C\uDFA8"),
    REGEX_GENERATOR("REGEX генератор \uD83E\uDDEC"),
    CONSOLE_JAVASCRIPT("Консоль Javascript \uD83D\uDDA5"),
    RAZRABOTCHIK_UI_UX("Разработчик UI/UX \uD83E\uDDD1\u200D\uD83D\uDCBB"),
    POISKOVAYA_SISTEMA_SOLR("Поисковая система Solr \uD83D\uDD0D"),
    IT_EXPERT("IT Эксперт \uD83D\uDCBC"),
    IT_ARCHITECTOR("IT Архитектор \uD83D\uDD79"),
    LIST_EXCEL("Лист Excel \uD83D\uDCCA"),
    KIBERBEZOPASNOST("Кибербезопасность \uD83D\uDD75\uFE0F"),
    TEHNICESKIJ_PISATEL("Технический писатель \uD83D\uDCDA"),
    WEB_DESIGNER("Веб дизайнер \uD83D\uDCBB"),
    UCHITEL_PHYLOSOPHII("Учитель философии \uD83D\uDC68\u200D\uD83C\uDFEB"),
    UCHITEL_MATEMATIKI("Учитель математики \uD83D\uDC69\u200D\uD83C\uDFEB"),
    GENERATOR_STARTUP_IDEI("Генератор стартап идей \uD83D\uDCA1"),
    TESTIROVSHIK_PO("Тестировщик ПО \uD83D\uDCBB"),
    SOKRAT("Сократ \uD83C\uDFFA"),
    PHILOSOPH("Философ \uD83E\uDDD0"),
    URIST_KONSULTANT("Юрист консультант ⚖\uFE0F"),
    POISK_SYNONIMOV("Поиск синонимов \uD83E\uDDE9"),
    POKUPATEL("Покупатель \uD83D\uDECD"),
    SHAHMATIST("Шахматист ♟"),
    ACADEMIC("Академик \uD83C\uDF93"),
    INFLUENCER("Инфлюенсер \uD83D\uDCF2"),
    AUTHOR_ESSE("Автор эссе \uD83D\uDD8B"),
    RESTORANNYI_CRITIC("Ресторанный критик \uD83C\uDF7D"),
    DIETOLOG("Диетолог \uD83C\uDF4F"),
    SUMASHEDHSHYJ("Сумасшедший \uD83E\uDD2A"),
    YOUG("Йог \uD83E\uDDD8\u200D♂\uFE0F"),
    ORATOR("Оратор \uD83D\uDCE2"),
    SMM_MANAGER("SMM менеджер\uD83D\uDC68\u200D\uD83D\uDCBC"),
    HYPNOLOG("Гипнолог \uD83D\uDE34"),
    JOURNALIST("Журналист \uD83D\uDCF0"),
    HUDOZHNIK_ASCII("Художник ASCII \uD83C\uDFA8"),
    LIFE_COUCH("Лайф коуч \uD83D\uDCA1"),
    DATAVIZ("Датавиз \uD83D\uDCC8"),
    ASTROLOG("Астролог ⭐\uFE0F"),
    VIZAZHIST("Визажист \uD83D\uDC84"),
    REQROUTER("Рекрутер \uD83E\uDD1D"),
    ETIMOLOG("Этимолог \uD83D\uDCD6"),
    COMMENTATOR("Комментатор \uD83C\uDF99"),
    KINOLOG("Кинолог \uD83D\uDC36"),
    LOGIST("Логист \uD83D\uDDFA"),
    SHEF_POVAR("Шеф повар \uD83E\uDDD1\u200D\uD83C\uDF73"),
    FLORIST("Флорист \uD83D\uDC90"),
    VOLSHEBNIK("Волшебник \uD83E\uDDD9\u200D♀\uFE0F"),
    RIELTOR("Риелтор \uD83C\uDFE1"),
    BUHGALTER("Бухгалтер \uD83D\uDCB0"),
    AUTOMEHANIC("Автомеханик \uD83D\uDD27"),
    APHORISMY("Афоризмы \uD83D\uDD8B"),
    TOLKOVATEL_SNOV("Толкователь снов ☁\uFE0F"),
    PEREVODCHICK("Переводчик \uD83C\uDF0D"),
    FUTBOLNYU_COMMENTATOR("Футбольный комментатор ⚽\uFE0F"),
    COMPOSITOR("Композитор \uD83C\uDFB5"),
    POET("Поэт \uD83D\uDCDC"),
    GENERATOR_PAROLEJ("Генератор паролей \uD83D\uDD10"),
    PUTEVODITEL("Путеводитель \uD83D\uDDFA"),
    STENDAP_COMIC("Стендап комик \uD83C\uDFAD"),
    PISATEL("Писатель \uD83D\uDD8A"),
    KINOCRITIC("Кинокритик \uD83C\uDFAC"),
    COUCH_PO_OTNOSHENIYAM("Коуч по отношениям \uD83D\uDC6B"),
    REKOMENDATEL_PESEN("Рекомендатель песен \uD83C\uDFB6"),
    DOCTOR("Доктор 👨‍⚕️"),
    STABLE_DIFFUSION_PROMPTY("Stable diffusion промпты 🌪"),
    AUTHOR_CONTENTA_V_SOC_SETYAH("Автор контента в соцсетях📱"),
    STRANICA_V_VIKIPEDII("Страница в википедии 📖"),
    META_GPT("META_GPT 🤖"),
    STOMATOLOG("Стоматолог 👩‍⚕️"),
    MIDJOURNEY_PROMPTY("Midjourney промпты 🛣"),
    YOU_TUBER("YouTuber 🎥"),
    TWITTER_MARKETOLOG("Твиттер маркетолог 🐦"),
    TIK_TOK_MARKETOLOG("Тик-Ток маркетолог 📱"),
    EXPERT_PO_PITANIY("Экспетр по питанию 🍏"),
    INFORMACIYA_O_TREKE("Информация о треке 🎼"),
    TIK_TOK_GPT_PRO("Тик-Ток GPT PRO 🤳"),
    VIRUSNAYA_REKLAMA("Вирусная реклама 📺"),
    PERSONALNYJ_VRACH("Персональный врач 👨‍⚕️"),
    EXPERT_PO_MUSIKE("Эксперт по музыке 🎷"),
    GENERATOR_PLEYLISTA("Генератор плейлиста 🎧"),
    KINO_SHOU_SOVETCHIK("Кино-шоу советчик 📽");


    private final String name;
    private final String prompt;
    private final String description;

    AssistantRole(String name, String prompt, String description) {
        this.name = name;
        this.prompt = prompt;
        this.description = description;
    }

    AssistantRole(String name, String prompt) {
        this.name = name;
        this.prompt = prompt;
        this.description = "Описание";
    }

    AssistantRole(String name) {
        this.name = name;
        this.prompt = "Ты - " + name;
        this.description = "Описание";
    }
}
