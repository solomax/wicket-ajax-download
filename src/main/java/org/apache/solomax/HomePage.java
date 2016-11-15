package org.apache.solomax;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.StringResourceStream;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	Logger log = LogManager.getLogger(HomePage.class);
	private long counter = 0;

	public HomePage(final PageParameters parameters) {
		super(parameters);

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		// Add a component to download a file without page refresh
		final AjaxDownload download = new AjaxDownload();
		add(download);

		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		final Form<Void> form = new Form<>("form");
		form.add(new AjaxButton("page-download") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				download.setFileName("test.txt");
				download.setResourceStream(new StringResourceStream("bla-bla-bla", "text/plain"));
				download.initiate(target);

				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});
		final AjaxDownload download1 = new AjaxDownload();
		form.add(download1);
		form.add(new AjaxButton("form-reload") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				counter++;
				target.add(form);
				target.appendJavaScript("$('#iframe-count').text('Currently on the page ' + $('iframe').length + ' iframes');");
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});
		form.add(new AjaxButton("form-download") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				download1.setFileName(String.format("test%s.txt", counter));
				download1.setResourceStream(new StringResourceStream(String.format("bla-bla-bla %s", counter), "text/plain"));
				download1.initiate(target);

				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});
		add(feedback.setOutputMarkupId(true), form.setOutputMarkupId(true));

		add(new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				System.err.println("!!!! Web socket should not be closed by Ajax download");
				log.error("!!!! Web socket should not be closed by Ajax download");
			}
		});
	}
}
