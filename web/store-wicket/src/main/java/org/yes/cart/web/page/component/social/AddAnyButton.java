package org.yes.cart.web.page.component.social;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.web.page.component.BaseComponent;

import javax.servlet.http.HttpServletRequest;

/**
 * User: denispavlov
 * Date: 13-03-17
 * Time: 8:36 AM
 */
public class AddAnyButton extends BaseComponent {

    private final Product product;
    private final String supplier;

    public AddAnyButton(final String id, final Product product, final String supplier) {
        super(id);
        this.product = product;
        this.supplier = supplier;
    }

    @Override
    protected void onBeforeRender() {

        final BookmarkablePageLink link = (BookmarkablePageLink) getWicketSupportFacade().links().newProductLink("link", supplier, product.getProductId());

        final String lang = getLocale().getLanguage();

        final CharSequence uri = link.urlFor(link.getPageClass(), link.getPageParameters());
        final HttpServletRequest req = (HttpServletRequest)((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();
        final String absUri = RequestUtils.toAbsolutePath(req.getRequestURL().toString(), uri.toString());

        final String name = getI18NSupport().getFailoverModel(product.getDisplayName(), product.getName()).getValue(lang);

        final StringBuilder anchor = new StringBuilder()
                .append("<a class=\"a2a_dd\" href=\"http://www.addtoany.com/share_save?linkurl=")
                .append(absUri)
                .append("&amp;linkname=")
                .append(name)
                .append("\">Share</a>");

        final StringBuilder js = new StringBuilder()
                .append("<script type=\"text/javascript\">\n")
                .append("            var a2a_config = a2a_config || {};\n")
                .append("            a2a_config.linkname = \"").append(name).append("\";\n")
                .append("            a2a_config.linkurl = \"").append(absUri).append("\";\n")
                .append("            a2a_config.locale = \"").append(lang).append("\";")
                .append("            a2a_config.color_main = \"D7E5ED\";")
                .append("            a2a_config.color_border = \"AECADB\";")
                .append("            a2a_config.color_link_text = \"333333\";")
                .append("            a2a_config.color_link_text_hover = \"333333\";")
                .append("</script>");

        addOrReplace(new Label("anchor", anchor.toString()).setEscapeModelStrings(false));
        addOrReplace(new Label("js", js.toString()).setEscapeModelStrings(false));

        super.onBeforeRender();
    }
}
