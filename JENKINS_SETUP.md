# Jenkins CI/CD Setup Guide

Since you have the "Jenkins Master" running but empty configuration, follow these steps to activate your pipelines.

## 1. Prerequisites (macOS)
Since you are running on macOS (likely via Homebrew or direct download), ensure the following:

### Docker Access
On macOS with Docker Desktop, the standard user usually has access.
*   **Verification**: Run `docker ps` in the terminal. If it lists containers (or empty list) without errors, you are good.
*   **Jenkins User**: If you run Jenkins as a macOS Service (launchd), it usually runs as your user or `jenkins`. If you run it via `jenkins-lts` command, it runs as **YOU**. This is the easiest way.

### Tools Configuration
Jenkins needs to know where your tools are.
*   **Java**: `/opt/homebrew/Cellar/openjdk@21/21.0.9/libexec/openjdk.jdk/Contents/Home` (from your `mvn -v`)
*   **Maven**: `/opt/homebrew/Cellar/maven/3.9.11/libexec`

### Kubernetes Config
Your local kubectl config needs to be accessible to Jenkins.
*   **If running Jenkins as YOU**: It will pick up `~/.kube/config` automatically.
*   **If running as 'jenkins' user**:
    ```bash
    sudo mkdir -p /Users/Shared/jenkins/.kube
    sudo cp ~/.kube/config /Users/Shared/jenkins/.kube/config
    sudo chown -R jenkins /Users/Shared/jenkins/.kube
    ```

    sudo chown -R jenkins /Users/Shared/jenkins/.kube
    ```

## 2. Global Tool Configuration (Fix "mvn not found")
Jenkins doesn't know where Homebrew installed your tools. You **MUST** configure this:

1. Go to **Manage Jenkins** -> **Tools**.
2. **JDK Installations**:
   - Click **Add JDK**.
   - **Name**: `jdk-21` (or just `Java`).
   - Uncheck "Install automatically".
   - **JAVA_HOME**: `/opt/homebrew/Cellar/openjdk@21/21.0.9/libexec/openjdk.jdk/Contents/Home`
3. **Maven Installations**:
   - Click **Add Maven**.
   - **Name**: `maven`.
   - Uncheck "Install automatically".
   - **MAVEN_HOME**: `/opt/homebrew/Cellar/maven/3.9.11/libexec`
4. **Save**.
5. **CRITICAL**: Go to **Manage Jenkins** -> **System**.
   - Scroll to **Global properties**.
   - Check **Environment variables**.
   - Add:
     - **Name**: `PATH`
     - **Value**: `/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin`
     - (⚠️ **DO NOT** use `$PATH` here, it breaks things. Use the full string above).
   - **Save**.

## 3. Required Plugins
Go to **Manage Jenkins > Plugins > Available Plugins** and install:
- **Pipeline** (installed by default usually).
- **Git** plugin.
- **Pipeline: GitHub** (for webhooks).
- **Credentials Binding** plugin.

## 3. Configure Credentials
The pipelines rely on a specific credential ID: `dockerhub-creds`.

1. Go to **Manage Jenkins > Credentials > System > Global credentials**.
2. Click **Add Credentials**.
3. **Kind**: Username with password.
4. **Scope**: Global.
5. **Username**: *Your DockerHub Username*.
6. **Password**: *Your DockerHub Access Token/Password*.
7. **ID**: `dockerhub-creds` (⚠️ CRITICAL: Must match exactly).
8. **Description**: DockerHub Credentials.

## 4. Create Service Jobs (Child Pipelines)
The Master pipeline triggers these jobs. You must create them **exactly** with these names.

For each service listed below:
1. **New Item** -> **Pipeline**.
2. Enter **Job Name** (see list below).
3. Scroll to **Pipeline** section:
   - **Definition**: Pipeline from SCM.
   - **SCM**: Git.
   - **Repository URL**: `https://github.com/valmikGit/Metro-Car-Pooling.git` (or your local path if testing locally).
   - **Branch**: `main`.
   - **Script Path**: (see list below).
4. **Save**.

| Service Config | Job Name in Jenkins | Script Path in Git |
| :--- | :--- | :--- |
| **Driver** | `driver-service` | `driver/Jenkinsfile` |
| **Rider** | `rider-service` | `rider/Jenkinsfile` |
| **User** | `user-service` | `user/Jenkinsfile` |
| **Matching** | `matching-service` | `matching/Jenkinsfile` |
| **Gateway** | `gateway-service` | `gateway/Jenkinsfile` |
| **Trip** | `trip-service` | `trip/Jenkinsfile` |
| **Notification**| `notification-service`| `notification/Jenkinsfile` |
| **Frontend** | `frontend-service` | `frontend/Jenkinsfile` |

## 5. Create Master Orchestrator Job
This is the main brain that listens for git commits.

1. **New Item** -> **Pipeline**.
2. **Job Name**: `master-orchestrator`.
3. **Pipeline**:
   - **Definition**: Pipeline from SCM.
   - **SCM**: Git.
   - **Repository URL**: `https://github.com/valmikGit/Metro-Car-Pooling.git`.
   - **Branch**: `main`.
   - **Script Path**: `Jenkinsfile.master`.
4. **Save**.

## 6. Run It
1. Click **Build Now** on `master-orchestrator`.
2. It will detect changes (since it's the first run, it might verify everything or do nothing until the next commit).
3. You can utilize the "Build with Parameters" to set `DEPLOY_ALL = true` to force a full deployment.
