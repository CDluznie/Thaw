package fr.umlv.thaw.data.chatter.bot.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;

import fr.umlv.thaw.data.chatter.bot.BotBehavior;
import fr.umlv.thaw.data.chatter.bot.BotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.ChattingContext;

public class GithubBotFactoyEntry implements BotFactoryEntry {

	@Override
	public String getName() {
		return "github-bot";
	}

	@Override
	public BotBehavior getBehavior(String... args) throws IOException {
		Objects.requireNonNull(args);
		if (args.length < 1) {
			throw new IOException("missing github argument for github bot");
		}
		return new BotBehavior() {
			
			@Override
			public List<String> react(ChattingContext chattingContext) {
				String[] tokens = chattingContext.lastMessage().split(" ");
				if (!tokens[0].equals("github")) {
					return Collections.emptyList();
				}
				if (tokens.length == 1) {
					return getHelp();
				}
				Github github = new RtGithub();
				Coordinates coords = new Coordinates.Simple(Objects.requireNonNull(args[0]));
				Repo repository = github.repos().get(coords);
				switch (tokens[1]) {
					case "link":
						return getLink(args[0]);
					case "workers":
						return getWorkers(repository);
					case "nissues":
						return getNIssues(repository);
					default:
						return getHelp();
				}
			}
		};
	}
	
	private List<String> getHelp() {
		return List.of("missing or bad argument",
				"link : show the link to the github project",
				"workers : show the workers on the github project",
				"nissues : show the number of issues of the github project");
	}
	
	private List<String> getLink(String github) {
		String link = "<a href=\"https://github.com/" + github + "\">" + github + "</a>";
		return List.of(link);
	}
	
	private List<String> getWorkers(Repo repository) {
		Iterator<User> iterator = repository.assignees().iterate().iterator();
		return List.of(StreamSupport
			.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
			.flatMap(x -> {
				try {
					return Stream.of(x.login());
				} catch (IOException e) {
					return Stream.empty();
				}
			})
			.collect(Collectors.joining(", ")));
	}
	
	private List<String> getNIssues(Repo repository) {
		Iterator<Issue> iterator = repository.issues().iterate(Collections.<String, String>emptyMap()).iterator();
		long n = StreamSupport
			.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
			.count();
		return List.of(n + " issues");
	}
	
}
