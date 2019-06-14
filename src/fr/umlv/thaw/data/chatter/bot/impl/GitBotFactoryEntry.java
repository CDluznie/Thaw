package fr.umlv.thaw.data.chatter.bot.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import fr.umlv.thaw.data.chatter.bot.BotBehavior;
import fr.umlv.thaw.data.chatter.bot.BotFactoryEntry;
import fr.umlv.thaw.data.chatter.bot.ChattingContext;

public class GitBotFactoryEntry implements BotFactoryEntry {

	@Override
	public String getName() {
		return "git-bot";
	}

	@Override
	public BotBehavior getBehavior(String... args) throws IOException {
		Objects.requireNonNull(args);
		if (args.length < 1) {
			throw new IOException("missing git argument for gitbot");
		}
		Repository repository = getGit(new File(Objects.requireNonNull(args[0])));
		return new BotBehavior() {

			@Override
			public List<String> react(ChattingContext chattingContext) {
				if (!chattingContext.lastMessage().equals("commits")) {
					return Collections.emptyList();
				}
				try (Git git = new Git(repository)) {
					return git
							.branchList()
							.call()
							.stream()
							.flatMap(branch -> streamOfBranch(branch, git, repository))
							.collect(Collectors.toList());
				} catch (GitAPIException e) {
					return List.of("an error occurs during the repository parsing");
				}
			}

		};
	}

	private Repository getGit(File file) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		return builder.setGitDir(file)
				.readEnvironment()
				.findGitDir()
				.build();
	}

	private Stream<String> streamOfBranch(Ref branch, Git git, Repository repository) {
		try {
			Iterable<RevCommit> commits = git.log().add(repository.resolve(branch.getName())).call();
			return StreamSupport.stream(commits.spliterator(), false)
					.flatMap(GitBotFactoryEntry::formatCommit);
		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			return Stream.of("can not get branch " + branch.getName() + " of repository");
		}
	}

	private static Stream<String> formatCommit(RevCommit commit) {
		return Stream.of(commit.getCommitterIdent().getName(),commit.getFullMessage());
	}

}
