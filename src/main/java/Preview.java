import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Arrays;

public class Preview implements Runnable{
    public void run(){
        String text = GUI.editorPane.getText();
//        GUI.editorPane2.setContentType("text/html");
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[]{TablesExtension.create()}));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
//        System.out.println(GUI.editorPane2.getText());
        Node document = parser.parse(text);
        String html = renderer.render(document);
        System.out.println(html);
        GUI.editorPane2.setText(html);
    }
}
