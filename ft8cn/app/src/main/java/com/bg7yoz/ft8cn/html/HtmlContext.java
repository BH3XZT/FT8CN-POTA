package com.bg7yoz.ft8cn.html;
/**
 * Http服务内容的出框架。
 * 0.93.2（POTA 特化）：整体 UI 现代化改造，增加 POTA 公园信息展示。
 * 0.93.3：日志网页支持昼夜模式与颜色模式切换（浏览器本地记忆）。
 *
 * @author BGY70Z / BH3XZT
 */

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.bg7yoz.ft8cn.BuildConfig;
import com.bg7yoz.ft8cn.GeneralVariables;
import com.bg7yoz.ft8cn.R;

public class HtmlContext {
    private static final String HTML_HEAD = "<!DOCTYPE html>\n<html lang=\"zh\" data-theme=\"dark\"><head><meta charset=\"UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "<title>FT8CN · POTA</title>\n" +
            "<style>\n" +
            ":root{--bg1:#070d18;--bg2:#0d1830;--card:#121d33;--card2:#0e1728;--line:rgba(120,180,255,.16);" +
            "--text:#dbe7ff;--muted:#8aa4c8;--accent:#38bdf8;--accent2:#7c3aed;--ok:#34d399;--bad:#f87171;" +
            "--btn-bg:rgba(56,189,248,.18);--input-bg:#0b1526;--th-bg:rgba(56,189,248,.10);}\n" +
            ":root[data-theme=light]{--bg1:#eef4ff;--bg2:#dce9fc;--card:#ffffff;--card2:#f6f9ff;" +
            "--line:rgba(30,70,130,.16);--text:#13233d;--muted:#5a7290;--accent:#1565c0;--accent2:#6d28d9;" +
            "--ok:#047857;--bad:#b91c1c;--btn-bg:rgba(21,101,192,.10);--input-bg:#ffffff;--th-bg:rgba(21,101,192,.08);}\n" +
            "*{box-sizing:border-box}\n" +
            "body{margin:0;padding:0 0 24px;background:radial-gradient(1200px 600px at 10% -10%,rgba(56,189,248,.18) 0%,transparent 60%)," +
            "linear-gradient(160deg,var(--bg1),var(--bg2));color:var(--text);font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto," +
            "'PingFang SC','Microsoft YaHei',Arial,sans-serif;font-size:14px;line-height:1.5;min-height:100vh}\n" +
            ".wrap{max-width:1180px;margin:0 auto;padding:0 14px}\n" +
            ".topbar{display:flex;flex-wrap:wrap;align-items:center;gap:10px;padding:14px 4px;border-bottom:1px solid var(--line);" +
            "margin-bottom:14px}\n" +
            ".brand{font-size:20px;font-weight:700;letter-spacing:.5px;background:linear-gradient(90deg,#60a5fa,#a78bfa,#22d3ee);" +
            "-webkit-background-clip:text;background-clip:text;color:transparent}\n" +
            ".brand .ver{font-size:12px;color:var(--muted);-webkit-text-fill-color:var(--muted);margin-left:6px}\n" +
            ".tag{display:inline-flex;align-items:center;gap:6px;padding:3px 10px;border-radius:999px;background:var(--btn-bg);" +
            "border:1px solid var(--line);color:var(--text);font-size:12px}\n" +
            ".tag.pota{background:rgba(52,211,153,.12);border-color:rgba(52,211,153,.4);color:var(--ok)}\n" +
            ".spacer{flex:1}\n" +
            "a{color:var(--accent);text-decoration:none}\n" +
            "a:hover{color:var(--accent2);text-decoration:underline}\n" +
            ".btn{display:inline-block;padding:7px 14px;border-radius:8px;border:1px solid var(--line);" +
            "background:linear-gradient(180deg,var(--btn-bg),transparent);color:var(--text);font-weight:600;font-size:13px}\n" +
            ".btn:hover{border-color:var(--accent);text-decoration:none}\n" +
            ".ctl{background:var(--input-bg);border:1px solid var(--line);color:var(--text);border-radius:8px;padding:6px 10px;font-size:13px}\n" +
            ".ctl:focus{outline:none;border-color:var(--accent)}\n" +
            ".card{background:linear-gradient(180deg,var(--card),var(--card2));border:1px solid var(--line);border-radius:12px;" +
            "padding:14px;margin:0 0 14px;box-shadow:0 8px 24px rgba(0,0,0,.12)}\n" +
            ".card h2{margin:0 0 10px;font-size:15px;font-weight:600;color:var(--text);display:flex;align-items:center;gap:8px}\n" +
            ".card h2:before{content:'';width:4px;height:16px;border-radius:2px;background:linear-gradient(180deg,var(--accent),var(--accent2))}\n" +
            ".grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(230px,1fr));gap:10px}\n" +
            ".grid a{display:block;padding:10px 12px;border-radius:10px;border:1px solid var(--line);background:var(--btn-bg);" +
            "color:var(--text);font-size:13px}\n" +
            ".grid a:hover{border-color:var(--accent);text-decoration:none}\n" +
            "table{width:100%;border-collapse:collapse;background:var(--card);border-radius:10px;overflow:hidden;" +
            "margin:8px 0;border:1px solid var(--line)}\n" +
            "td,th{padding:7px 9px;border-bottom:1px solid var(--line);font-size:13px;text-align:left;vertical-align:middle}\n" +
            "th{background:var(--th-bg);color:var(--text);font-weight:600;white-space:nowrap}\n" +
            "tr:nth-child(even) td{background:rgba(127,127,127,.04)}\n" +
            "tr:hover td{background:var(--th-bg)}\n" +
            "td.title{background:linear-gradient(90deg,var(--th-bg),transparent);color:var(--text);font-weight:700;font-size:14px}\n" +
            "td.default{color:var(--text)}\n" +
            "input,select,textarea{background:var(--input-bg);border:1px solid var(--line);color:var(--text);border-radius:8px;" +
            "padding:6px 9px;font-size:13px;outline:none}\n" +
            "input:focus,select:focus{border-color:var(--accent);box-shadow:0 0 0 3px rgba(56,189,248,.15)}\n" +
            "input[type=submit],button{padding:7px 16px;border-radius:8px;border:1px solid var(--line);cursor:pointer;" +
            "background:linear-gradient(180deg,var(--btn-bg),transparent);color:var(--text);font-weight:600}\n" +
            "input[type=submit]:hover,button:hover{border-color:var(--accent)}\n" +
            "fieldset{border:1px solid var(--line);border-radius:10px;padding:10px 12px;margin:6px 0}\n" +
            "legend{color:var(--muted);font-size:12px;padding:0 6px}\n" +
            ".muted{color:var(--muted);font-size:12px}\n" +
            ".footer{margin-top:20px;padding-top:12px;border-top:1px solid var(--line);color:var(--muted);font-size:12px;" +
            "display:flex;flex-wrap:wrap;gap:10px;align-items:center}\n" +
            ".warn{color:#f59e0b}\n" +
            "font[color=green]{color:var(--ok)}\n" +
            "font[color=red]{color:var(--bad)}\n" +
            "@media(max-width:560px){.brand{font-size:17px}td,th{padding:5px 6px;font-size:12px}.grid{grid-template-columns:1fr}}\n" +
            "</style></head><body><div class=\"wrap\">\n";

    private static String htmlTitle() {
        String park = GeneralVariables.getPotaPark();
        String callsign = GeneralVariables.myCallsign;
        return "<div class=\"topbar\">"
                + "<div class=\"brand\">FT8CN <span class=\"ver\">v" + BuildConfig.VERSION_NAME + " · POTA</span></div>"
                + "<div class=\"tag\">" + GeneralVariables.getStringFromResource(R.string.html_my_callsign)
                + " " + (callsign.length() > 0 ? callsign : "-") + "</div>"
                + (park.length() > 0 ? "<div class=\"tag pota\">POTA " + park + "</div>" : "")
                + "<div class=\"spacer\"></div>"
                + "<select id=\"colorSel\" class=\"ctl\" title=\"Color mode\">"
                + "<option value=\"#38bdf8\">Blue</option>"
                + "<option value=\"#22d3ee\">Cyan</option>"
                + "<option value=\"#a78bfa\">Purple</option>"
                + "<option value=\"#34d399\">Green</option>"
                + "<option value=\"#fbbf24\">Orange</option>"
                + "<option value=\"#f87171\">Red</option>"
                + "</select>"
                + "<button id=\"themeBtn\" class=\"ctl\" title=\"Day / Night\">🌙</button>"
                + "<a class=\"btn\" href=\"/\">" + GeneralVariables.getStringFromResource(R.string.html_return) + "</a>"
                + "</div>\n";
    }

    private static final String WEB_THEME_SCRIPT =
            "<script>\n(function(){\n" +
                    "var root=document.documentElement;\n" +
                    "var savedTheme=localStorage.getItem('ft8cn.web.theme');\n" +
                    "var savedAccent=localStorage.getItem('ft8cn.web.accent')||'#38bdf8';\n" +
                    "if(!savedTheme){savedTheme=(window.matchMedia&&window.matchMedia('(prefers-color-scheme: dark)').matches)?'dark':'light';}\n" +
                    "function apply(){root.setAttribute('data-theme',savedTheme);root.style.setProperty('--accent',savedAccent);" +
                    "var b=document.getElementById('themeBtn');if(b){b.textContent=(savedTheme==='dark')?'☀️':'🌙';}" +
                    "var s=document.getElementById('colorSel');if(s){s.value=savedAccent;}}\n" +
                    "function toggle(){savedTheme=(savedTheme==='dark')?'light':'dark';localStorage.setItem('ft8cn.web.theme',savedTheme);apply();}\n" +
                    "apply();\n" +
                    "document.addEventListener('DOMContentLoaded',function(){\n" +
                    "  apply();\n" +
                    "  var b=document.getElementById('themeBtn');if(b){b.onclick=toggle;}\n" +
                    "  var s=document.getElementById('colorSel');if(s){s.onchange=function(){savedAccent=s.value;" +
                    "localStorage.setItem('ft8cn.web.accent',savedAccent);apply();};}\n" +
                    "});\n})();\n</script>\n";

    private static String htmlFooter() {
        return "<div class=\"footer\"><span>BG7YOZ · FT8CN</span>"
                + "<span>" + GeneralVariables.getStringFromResource(R.string.fork_info_line1)
                + "<br>" + GeneralVariables.getStringFromResource(R.string.fork_info_line2) + "</span>"
                + "<div class=\"spacer\"></div><a href=\"/\">"
                + GeneralVariables.getStringFromResource(R.string.html_return) + "</a></div>\n</div>\n"
                + WEB_THEME_SCRIPT;
    }

    private static String HTML_BODY(String context) {
        return htmlTitle() + context + "\n" + htmlFooter() + "</body></html>";
    }

    public static String HTML_STRING(String context) {
        return HTML_HEAD + HTML_BODY(context);
    }


    public static String DEFAULT_HTML() {
        String park = GeneralVariables.getPotaPark().length() > 0
                ? GeneralVariables.getPotaPark() : "-";
        StringBuilder sb = new StringBuilder();
        //POTA 专属卡片：仅在设置了公园编号时显示
        if (GeneralVariables.getPotaPark().length() > 0) {
            sb.append("<div class=\"card\"><h2>POTA · ").append(GeneralVariables
                            .getStringFromResource(R.string.html_pota_export_title)).append("</h2>")
                    .append("<div class=\"muted\">").append(GeneralVariables
                            .getStringFromResource(R.string.html_pota_hint)).append("</div><br>")
                    .append("<div class=\"tag pota\">").append(GeneralVariables
                            .getStringFromResource(R.string.pota_park)).append(": ").append(park).append("</div>")
                    .append(" <a class=\"btn\" href=\"/POTA\">")
                    .append(GeneralVariables.getStringFromResource(R.string.html_pota_export_title)).append("</a>")
                    .append("</div>");
        }
        //常规功能卡片
        sb.append("<div class=\"card\"><h2>FT8CN</h2><div class=\"grid\">");
        appendLink(sb, "/debug", R.string.html_track_operation_information);
        appendLink(sb, "/showHash", R.string.html_track_callsign_hash_table);
        appendLink(sb, "/newMessage", R.string.html_trace_parsed_messages);
        appendLink(sb, "/callsignGrid", R.string.html_trace_callsign_and_grid_correspondence_table);
        appendLink(sb, "/message", R.string.html_query_swl_message);
        appendLink(sb, "/QSOSWLMSG", R.string.html_query_qso_swl);
        appendLink(sb, "/config", R.string.html_query_configuration_information);
        appendLink(sb, "/allTable", R.string.html_query_all_table);
        appendLink(sb, "/followCallsigns", R.string.html_manage_tracking_callsign);
        appendLink(sb, "/showQSLCallsigns", R.string.html_show_communication_callsigns);
        appendLink(sb, "/getCallsignQTH", R.string.html_callsign_qth);
        appendLink(sb, "/QSOLogs", R.string.html_query_logs);
        appendLink(sb, "/QSLTable", R.string.html_export_log);
        appendLink(sb, "/ImportLog", R.string.html_import_log);
        sb.append("</div></div>");
        return HTML_STRING(sb.toString());
    }

    private static void appendLink(StringBuilder sb, String href, int textRes) {
        sb.append("<a href=\"").append(href).append("\">")
                .append(GeneralVariables.getStringFromResource(textRes)).append("</a>");
    }

    /**
     * 生成表格的内容
     *
     * @param sb html
     * @param s  内容
     * @return html
     */
    public static StringBuilder tableCell(StringBuilder sb, String... s) {
        for (String c : s) {
            sb.append(String.format("<td>%s</td>", c));
        }
        sb.append("\n");
        return sb;
    }

    /**
     * 生成表格标题
     *
     * @param sb html
     * @param s  标题
     * @return html
     */
    public static StringBuilder tableCellHeader(StringBuilder sb, String... s) {
        for (String c : s) {
            sb.append(String.format("<th>%s</th>", c));
        }
        sb.append("\n");
        return sb;
    }

    @SuppressLint("DefaultLocale")
    public static StringBuilder tableKeyRow(StringBuilder sb, Boolean darkMode, String key, int value) {
        sb.append(String.format("<tr %s><td>%s</td><td>%d</td></tr>\n"
                , darkMode ? "class=\"bbb\"" : ""
                , key, value));
        return sb;
    }

    public static StringBuilder tableKeyRow(StringBuilder sb, Boolean darkMode, String key, String value) {
        sb.append(String.format("<tr %s><td>%s</td><td>%s</td></tr>\n"
                , darkMode ? "class=\"bbb\"" : ""
                , key, value));
        return sb;
    }

    public static StringBuilder tableRowBegin(StringBuilder sb) {
        return tableRowBegin(sb, false, false);
    }

    public static StringBuilder tableRowBegin(StringBuilder sb, Boolean alignCenter, boolean darkMode) {
        sb.append(String.format("<tr %s %s>"
                , alignCenter ? "align=center" : ""
                , darkMode ? "class=\"bbb\"" : ""));
        return sb;
    }

    public static StringBuilder tableRowEnd(StringBuilder sb) {
        sb.append("</tr>");
        return sb;
    }

    public static StringBuilder tableBegin(StringBuilder sb) {
        return tableBegin(sb, false, 1, true);
    }

    public static StringBuilder tableBegin(StringBuilder sb, boolean hasBorder, boolean fullWidth) {
        return tableBegin(sb, hasBorder, 1, fullWidth);
    }

    @SuppressLint("DefaultLocale")
    public static StringBuilder tableBegin(StringBuilder sb, boolean hasBorder, int cellpadding, boolean fullWidth) {
        sb.append(String.format("<table bgcolor=#a1a1a1 %s cellpadding=\"%d\" cellspacing=\"0\" %s >"
                , hasBorder ? "border=\"1\"" : ""
                , cellpadding
                , fullWidth ? "width=\"100%\"" : ""));
        return sb;
    }

    public static StringBuilder tableEnd(StringBuilder sb) {
        sb.append("</table>");
        return sb;
    }

    public static String ListTableContext(Cursor cursor, boolean fullWidth) {
        return ListTableContext(cursor, false, 0, fullWidth);
    }

    public static String ListTableContext(Cursor cursor, boolean hasBorder, int cellpadding, boolean fullWidth) {
        StringBuilder result = new StringBuilder();
        HtmlContext.tableBegin(result, hasBorder, cellpadding, fullWidth).append("\n");

        //写字段名
        HtmlContext.tableRowBegin(result);
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            HtmlContext.tableCellHeader(result, cursor.getColumnName(i));
        }
        HtmlContext.tableRowEnd(result).append("\n");
        int order = 0;
        while (cursor.moveToNext()) {
            HtmlContext.tableRowBegin(result, false, order % 2 != 0);
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                HtmlContext.tableCell(result, (cursor.getString(i) != null) ? cursor.getString(i) : "");
            }
            HtmlContext.tableRowEnd(result).append("\n");
            order++;
        }
        HtmlContext.tableEnd(result).append("\n");
        cursor.close();
        return result.toString();
    }

}
