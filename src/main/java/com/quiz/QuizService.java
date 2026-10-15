
package com.quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.quiz.QuizModels.*;
import com.quiz.QuizModels.AnswerSubmission;
import com.quiz.QuizModels.AttemptView;
import com.quiz.QuizModels.LeaderboardEntry;
import com.quiz.QuizModels.QuestionView;
import com.quiz.QuizModels.QuizView;
import com.quiz.QuizModels.ReviewItem;
import com.quiz.QuizModels.SubmitRequest;
import com.quiz.QuizModels.SubmitResponse;

@Service
public class QuizService {

    private static final int QUESTIONS_PER_ATTEMPT = 10;

    private record Question(
            String id, String prompt, List<String> options,
            String answer, String explanation) {}

    private record Quiz(
            String id, String title, String topic,
            String difficulty, List<Question> questions) {}

    private final List<AttemptView> attempts = new CopyOnWriteArrayList<>();
    private final Map<String, Set<LocalDate>> studentDays = new HashMap<>();
    private final AtomicLong ids = new AtomicLong(1);

    private static Question q(String id, String prompt,
            String a, String b, String c, String d,
            String answer, String explanation) {
        return new Question(id, prompt, List.of(a, b, c, d),
                answer, explanation);
    }

    private static Quiz quiz(String id, String title, String topic,
            String difficulty, Question... questions) {
        return new Quiz(id, title, topic, difficulty, List.of(questions));
    }

    private final List<Quiz> quizzes = List.of(

        // 1. WEB BASICS — EASY (16 questions)
        quiz("web-easy", "Web Basics", "Web Technology", "Easy",
            q("w1","Which language adds interactivity to web pages?","HTML","CSS","JavaScript","SQL","JavaScript","JavaScript responds to user actions."),
            q("w2","Which tag creates a hyperlink?","<link>","<a>","<href>","<url>","<a>","The anchor tag creates hyperlinks."),
            q("w3","Which CSS property changes text colour?","font-style","background","color","text-size","color","The color property sets text colour."),
            q("w4","Which is the highest-level HTML heading?","<heading>","<h6>","<head>","<h1>","<h1>","h1 is the top heading level."),
            q("w5","Which attribute provides alternative text for an image?","title","alt","src","href","alt","Alt text describes an image."),
            q("w6","Which CSS layout system is primarily one-dimensional?","Flexbox","SQL","DOM","JSON","Flexbox","Flexbox arranges items in a row or column."),
            q("w7","Which element creates an HTML form?","<form>","<inputbox>","<fieldsettext>","<data>","<form>","The form element groups input controls."),
            q("w8","Which keyword declares a reassignable block-scoped JavaScript variable?","const","let","static","define","let","let declares a block-scoped variable."),
            q("w9","What does CSS stand for?","Computer Style Sheets","Creative Styling Syntax","Cascading Style Sheets","Coded Sheet System","Cascading Style Sheets","CSS means Cascading Style Sheets."),
            q("w10","Which HTTP method commonly retrieves a resource?","POST","PATCH","DELETE","GET","GET","GET requests a resource."),
            q("w11","Which HTML attribute specifies a link destination?","src","href","action","target-id","href","href specifies the destination URL."),
            q("w12","Which JavaScript method selects an element by ID?","querySelectorAll()","getElementById()","getById()","getClass()","getElementById()","It selects an element using its ID."),
            q("w13","Which CSS unit is relative to the root font size?","px","rem","cm","vh","rem","rem is relative to the root element."),
            q("w14","What is the DOM?","Database engine","Browser extension","Document tree representation","CSS preprocessor","Document tree representation","The DOM represents a document as a node tree."),
            q("w15","Which HTML element represents navigation links?","<nav>","<navigate>","<links>","<url>","<nav>","nav represents a navigation section."),
            q("w16","Which HTTP status code indicates success?","301","403","200","500","200","200 indicates a successful request.")
        ),

        // 2. GIT AND DEVOPS — MEDIUM (16 questions)
        quiz("git-medium", "Git & DevOps", "DevOps", "Medium",
            q("g1","Which command records staged changes?","git push","git commit","git pull","git clone","git commit","commit creates a local snapshot."),
            q("g2","What is Jenkins mainly used for?","Image editing","CI/CD automation","Database design","Browsing","CI/CD automation","Jenkins automates build and test pipelines."),
            q("g3","Which command copies a remote repository locally?","git init","git status","git clone","git diff","git clone","clone downloads a repository."),
            q("g4","What does continuous integration encourage?","Large infrequent merges","Frequent integration and automated checks","Manual deployment only","Avoiding Git","Frequent integration and automated checks","CI validates frequently integrated changes."),
            q("g5","What is a Git branch?","Independent development line","Repository deletion","Encryption method","Remote server","Independent development line","Branches allow work to proceed separately."),
            q("g6","Which command shows changed and untracked files?","git log","git status","git merge","git tag","git status","status displays working-tree information."),
            q("g7","What is pom.xml used for in Maven?","Browser cookies","Project configuration and dependencies","Git branches","Operating system settings","Project configuration and dependencies","The POM defines dependencies and build settings."),
            q("g8","What is a Docker image?","Only a running container","Template used to create containers","Git commit","Network cable","Template used to create containers","Containers are created from images."),
            q("g9","What is a Docker container?","Running isolated instance of an image","Source repository","Java compiler","Test report","Running isolated instance of an image","Containers run applications in isolated environments."),
            q("g10","Why include automated tests in a CI pipeline?","Verify changes automatically","Rename files","Disable version control","Remove dependencies","Verify changes automatically","Tests detect regressions."),
            q("g11","What does git pull generally do?","Deletes commits","Fetches and integrates remote changes","Creates a website","Lists ignored files","Fetches and integrates remote changes","pull fetches and integrates changes."),
            q("g12","Why should secrets not be hard-coded in a repository?","Only increases image size","Can expose credentials","Stops compilation","Prevents branching","Can expose credentials","Repository readers may access embedded secrets."),
            q("g13","What is a Dockerfile?","Instructions for building an image","SQL result file","Browser history","Git repository","Instructions for building an image","Docker uses its instructions to build images."),
            q("g14","What does CD commonly mean in CI/CD?","Code deletion","Continuous delivery or deployment","Central directory","Compiled data","Continuous delivery or deployment","CD automates delivery or deployment."),
            q("g15","Why use Git?","Track changes and collaborate","Replace all tests","Host only databases","Prevent code changes","Track changes and collaborate","Git records project history."),
            q("g16","What is a build artifact?","Output produced by a build","Deleted branch","Firewall rule","Keyboard shortcut","Output produced by a build","A JAR file is an example of an artifact.")
        ),

        // 3. CYBERSECURITY — HARD (16 questions)
        quiz("security-hard", "Cybersecurity Fundamentals", "Cybersecurity", "Hard",
            q("s1","What best reduces SQL injection risk?","String concatenation","Parameterized queries","Longer URLs","Disabling logs","Parameterized queries","Parameters separate data from SQL instructions."),
            q("s2","What is least privilege?","Everyone has admin rights","Only necessary permissions are granted","No passwords are used","Accounts are shared","Only necessary permissions are granted","Users get only the access they need."),
            q("s3","What is the purpose of Content Security Policy?","Compress images","Restrict permitted content sources","Store passwords","Replace HTTPS","Restrict permitted content sources","CSP limits allowed sources of browser content."),
            q("s4","What is multi-factor authentication?","Two identical passwords","Factors from at least two categories","Daily username changes","Double encryption only","Factors from at least two categories","MFA combines different authentication factor types."),
            q("s5","Which security property protects against unauthorized data alteration?","Availability","Integrity","Confidentiality","Compression","Integrity","Integrity concerns data accuracy and completeness."),
            q("s6","What does a cryptographic hash provide for integrity checks?","Original plaintext recovery","Fixed-length digest for comparison","Access control","Reversible compression","Fixed-length digest for comparison","Changed data generally produces a different digest."),
            q("s7","What is phishing?","Deceptive attempt to steal information","Port scanning","Load balancing","Tokenization","Deceptive attempt to steal information","Phishing tricks people into unsafe actions."),
            q("s8","Why use a salted password-hashing function?","Make passwords visible","Slow guessing and distinguish identical passwords","Email passwords","Remove authentication","Slow guessing and distinguish identical passwords","Salts and suitable password hashes hinder offline guessing."),
            q("s9","What is cross-site scripting (XSS)?","Attacker-controlled script runs in a user's browser","Backup encryption","Cable attack","DNS availability feature","Attacker-controlled script runs in a user's browser","XSS exploits untrusted script content."),
            q("s10","What is the primary goal of a DoS attack?","Make a service unavailable","Improve encryption","Rotate credentials","Validate backups","Make a service unavailable","DoS attempts to exhaust resources or disrupt service."),
            q("s11","What protects sensitive data in transit?","Plain HTTP","Correctly configured TLS","Disabled certificate checks","Public private keys","Correctly configured TLS","TLS can protect data confidentiality and integrity in transit."),
            q("s12","What is a zero-day vulnerability?","A flaw already patched for years","A flaw without an available effective patch","Failed login","Scheduled backup","A flaw without an available effective patch","Zero-day flaws may be exploited before a fix is available."),
            q("s13","What does a SIEM primarily do?","Collect and correlate security events","Replace every security control","Create layouts","Compile Java","Collect and correlate security events","SIEM supports detection and investigation."),
            q("s14","What is a vulnerability assessment for?","Identify and prioritize weaknesses","Guarantee no attacks","Remove patching needs","Publish passwords","Identify and prioritize weaknesses","Findings help teams prioritize remediation."),
            q("s15","What does a digital certificate help bind together?","Identity and public key","Keyboard layout","Backup schedule","Compression ratio","Identity and public key","Certificates associate identities with public keys."),
            q("s16","What is a benefit of network segmentation?","Limit connectivity and lateral movement","Make every host public","Disable monitoring","Remove authentication","Limit connectivity and lateral movement","Segmentation restricts traffic between network areas.")
        ),

        // 4. NETWORKING AND LINUX — MEDIUM (16 questions)
        quiz("network-linux-medium", "Networking & Linux", "Networking and Linux", "Medium",
            q("n1","Which protocol resolves domain names?","DNS","FTP","SSH","SMTP","DNS","DNS resolves names into records such as IP addresses."),
            q("n2","Which Linux command lists directory contents?","pwd","ls","cd","whoami","ls","ls lists files and directories."),
            q("n3","Which protocol provides reliable ordered delivery?","UDP","IP","TCP","ARP","TCP","TCP provides reliable, ordered byte-stream delivery."),
            q("n4","What does a default gateway do?","Routes traffic to other networks","Changes file permissions","Stores DNS only","Encrypts files","Routes traffic to other networks","The gateway forwards traffic outside the local subnet."),
            q("n5","Which command prints the current working directory?","cd","pwd","mkdir","touch","pwd","pwd displays the current path."),
            q("n6","What does SSH provide?","Secure remote login","Unencrypted browsing","Domain registration","Compression only","Secure remote login","SSH encrypts remote administration sessions."),
            q("n7","Which is a private IPv4 address?","8.8.8.8","172.20.5.4","1.1.1.1","203.0.113.9","172.20.5.4","172.16.0.0 to 172.31.255.255 is private."),
            q("n8","Which Linux command changes permissions?","chmod","grep","ping","uname","chmod","chmod modifies file permissions."),
            q("n9","What is DHCP used for?","Automatically assign IP configuration","Encrypt application data","Translate URLs to HTML","Monitor temperature","Automatically assign IP configuration","DHCP can provide IP, gateway and DNS settings."),
            q("n10","What is the default HTTPS port?","21","25","80","443","443","HTTPS commonly uses port 443."),
            q("n11","Which Linux command searches text patterns?","grep","mv","rmdir","date","grep","grep searches text for matching patterns."),
            q("n12","What does IPv4 CIDR /24 mean?","24 host bits","24-bit network prefix","24 networks","24-byte address","24-bit network prefix","The prefix has 24 network bits."),
            q("n13","Which command displays process information?","ps","cat","echo","clear","ps","ps displays process information."),
            q("n14","What does ARP resolve on a local IPv4 network?","IPv4 address to link-layer address","Name to certificate","Port to process","Username to password","IPv4 address to link-layer address","ARP discovers the local link-layer address."),
            q("n15","Which Linux permission allows executing a file?","Read","Execute","Write","None","Execute","Execute permission allows running a file when other requirements are met."),
            q("n16","Which tool commonly tests reachability using ICMP echo?","ping","chmod","tar","nano","ping","ping sends ICMP echo requests.")
        ),

        // 5. AI AND MACHINE LEARNING — HARD (16 questions)
        quiz("aiml-hard", "AI & Machine Learning", "Artificial Intelligence and Machine Learning", "Hard",
            q("a1","Which learning type uses labelled examples?","Unsupervised","Supervised","Reinforcement only","Dimensionality reduction","Supervised","Supervised learning uses labelled training examples."),
            q("a2","What is the main goal of PCA?","Increase features","Represent variance with fewer dimensions","Turn values into labels","Guarantee perfect classification","Represent variance with fewer dimensions","PCA projects data onto principal components."),
            q("a3","Which metric is TP divided by TP plus FP?","Recall","Precision","Specificity","Accuracy","Precision","Precision measures the correctness of predicted positives."),
            q("a4","What is overfitting?","Good training performance but poor generalization","No model parameters","No training","Always predicting majority class","Good training performance but poor generalization","The model learns patterns that do not generalize."),
            q("a5","Which is a common unsupervised clustering algorithm?","K-means","Linear regression","Naive Bayes","Logistic regression","K-means","K-means groups observations into clusters."),
            q("a6","Why use a separate test set?","Repeatedly tune every parameter","Estimate performance on held-out data","Replace training completely","Guarantee zero error","Estimate performance on held-out data","The test set evaluates data not used for fitting."),
            q("a7","What guides parameter updates in gradient descent?","Gradient of the loss","File name","Label count only","Display colour","Gradient of the loss","Gradient descent updates parameters to reduce loss."),
            q("a8","What does high recall indicate?","Most actual positives were identified","All predictions are correct","No false positives","Zero training time","Most actual positives were identified","Recall measures true positives among actual positives."),
            q("a9","What is regularization intended to do?","Reduce overfitting by discouraging complexity","Always maximize training accuracy","Remove every label","Make features identical","Reduce overfitting by discouraging complexity","Regularization penalizes excessive model complexity."),
            q("a10","Which activation function is common in hidden layers?","ReLU","Mean squared error","K-means","PCA","ReLU","ReLU applies max(0,x) and adds nonlinearity."),
            q("a11","What does a confusion matrix summarize?","Predicted versus actual class counts","Only training time","File order","Image sizes","Predicted versus actual class counts","It compares predictions with actual labels."),
            q("a12","Why scale features for some distance-based algorithms?","Avoid scale alone dominating distance","Turn features into labels","Guarantee correct clusters","Remove training data","Avoid scale alone dominating distance","Large-scale features can otherwise dominate distances."),
            q("a13","What does a decision-tree split do?","Partitions data to improve group quality","Encrypt examples","Increase missing values","Create network packets","Partitions data to improve group quality","A split separates samples based on a feature."),
            q("a14","What does a loss function measure?","Prediction error for optimization","Permanent data storage","Operating system choice","Fairness guarantee","Prediction error for optimization","Training aims to minimize the loss."),
            q("a15","What is an epoch in neural-network training?","One complete pass through training data","One feature","One test prediction","One neuron","One complete pass through training data","An epoch processes the training dataset once."),
            q("a16","What problem can occur in deep neural networks?","Vanishing gradients","DNS cache poisoning only","IP exhaustion only","CSS conflicts","Vanishing gradients","Small gradients can make early layers hard to train.")
        )
    );

    private List<Question> attemptQuestions(Quiz quiz) {
        return quiz.questions.subList(
                0, Math.min(QUESTIONS_PER_ATTEMPT, quiz.questions.size()));
    }

    public List<QuizView> listQuizzes(String difficulty) {
        return quizzes.stream()
                .filter(q -> difficulty == null || difficulty.isBlank()
                        || q.difficulty.equalsIgnoreCase(difficulty))
                .map(this::toView)
                .toList();
    }

    public QuizView getQuiz(String id) {
        return quizzes.stream()
                .filter(q -> q.id.equals(id))
                .findFirst()
                .map(this::toView)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
    }

    private QuizView toView(Quiz quiz) {
        List<QuestionView> questions = attemptQuestions(quiz).stream()
                .map(q -> new QuestionView(q.id, q.prompt, q.options))
                .toList();

        return new QuizView(
                quiz.id, quiz.title, quiz.topic, quiz.difficulty, questions);
    }

    public synchronized SubmitResponse submit(SubmitRequest request) {
        if (request.studentName() == null || request.studentName().isBlank()) {
            throw new IllegalArgumentException("Please enter a student name.");
        }

        Quiz quiz = quizzes.stream()
                .filter(q -> q.id.equals(request.quizId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        Map<String, String> selected = request.answers() == null
                ? Map.of()
                : request.answers().stream().collect(Collectors.toMap(
                        AnswerSubmission::questionId,
                        AnswerSubmission::answer,
                        (first, second) -> second));

        List<ReviewItem> review = new ArrayList<>();
        int score = 0;

        for (Question q : attemptQuestions(quiz)) {
            String answer = selected.getOrDefault(q.id, "");
            boolean correct = q.answer.equals(answer);

            if (correct) {
                score++;
            }

            review.add(new ReviewItem(
                    q.id, q.prompt, answer, q.answer, correct, q.explanation));
        }

        String name = request.studentName().trim();
        LocalDate today = LocalDate.now();

        AttemptView attempt = new AttemptView(
                Long.toString(ids.getAndIncrement()),
                name,
                quiz.title,
                quiz.difficulty,
                score,
                review.size(),
                score * 100.0 / review.size(),
                today,
                List.copyOf(review));

        attempts.add(attempt);
        studentDays.computeIfAbsent(
                name.toLowerCase(Locale.ROOT), k -> new HashSet<>()).add(today);

        return new SubmitResponse(attempt, leaderboard(), streak(name));
    }

    public List<AttemptView> attemptsFor(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }

        return attempts.stream()
                .filter(a -> a.studentName().equalsIgnoreCase(name))
                .sorted(Comparator.comparing(AttemptView::date).reversed()
                        .thenComparing(AttemptView::id, Comparator.reverseOrder()))
                .toList();
    }

    public synchronized List<LeaderboardEntry> leaderboard() {
        Map<String, List<AttemptView>> byStudent = attempts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.studentName().toLowerCase(Locale.ROOT)));

        List<LeaderboardEntry> entries = new ArrayList<>();

        for (List<AttemptView> studentAttempts : byStudent.values()) {
            AttemptView best = studentAttempts.stream()
                    .max(Comparator.comparingDouble(AttemptView::percentage)
                            .thenComparingInt(AttemptView::score))
                    .orElseThrow();

            String name = studentAttempts.get(0).studentName();

            entries.add(new LeaderboardEntry(
                    0, name, best.score(), best.total(),
                    studentAttempts.size(), streak(name)));
        }

        entries.sort(Comparator.comparingInt(LeaderboardEntry::bestScore)
                .reversed()
                .thenComparing(LeaderboardEntry::studentName,
                        String.CASE_INSENSITIVE_ORDER));

        List<LeaderboardEntry> ranked = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e = entries.get(i);
            ranked.add(new LeaderboardEntry(
                    i + 1, e.studentName(), e.bestScore(),
                    e.totalQuestions(), e.attempts(), e.streak()));
        }

        return ranked;
    }

    public synchronized int streak(String name) {
        Set<LocalDate> days =
                studentDays.get(name.toLowerCase(Locale.ROOT));

        if (days == null || days.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate cursor = days.contains(today) ? today : today.minusDays(1);

        if (!days.contains(cursor)) {
            return 0;
        }

        int streak = 0;

        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }
}
