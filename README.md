<div align="center">
  <img width="100%" src="https://capsule-render.vercel.app/api?type=blur&height=280&color=0:d8dee9,100:2e3440&text=Rush%20Hour%20Solver%20%E2%9C%A8&fontColor=81a1c1&fontSize=50&animation=twinkling&" />
</div>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Completed-green" />
  <img src="https://img.shields.io/badge/Recent_Build-Release-brightgreen" />
  <img src="https://img.shields.io/badge/Version-1.0.0-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-yellowgreen" />
  <img src="https://img.shields.io/badge/Built_With-Java-blue" />
</p>

<h1 align="center">
  <img src="https://readme-typing-svg.herokuapp.com?font=Fira+Code&pause=500&color=81a1c1&center=true&vCenter=true&width=600&lines=Muhammad+Kinan+Arkansyaddad;Rafael+Marchel+Darma+Wijaya" alt="Typing SVG" />
</h1>

---

## 📦 Table of Contents

- [✨ Overview](#-overview)
- [⚙️ Features](#️-features)
- [📸 Preview](#-preview)
- [📥 Installation](#-installation)
- [🚀 Usage](#-usage)
- [📂 Project Structure](#-project-structure)
- [🛣️ Roadmap](#️-roadmap)
- [🧠 Contributing](#-contributing)
- [👤 Author](#-author)

---

## ✨ Overview
**DoIt**: Your terminal, your flow — organized and programmable.

**DoIt** is a simple yet powerful CLI tool to manage terminal-based development workspaces using `tmux`, templates, and boilerplates.

No more tedious terminal command. Just define it once — then **DoIt**.

---

## ⚙️ Features

- **🧩 Template-based Layouts**: Define your workspace once, use it everywhere
- **🪄 One Command Setup**: Launch complex multi-window, multi-pane environments instantly
- **💾 Session Persistence**: Built-in integration with tmux-resurrect
- **📁 Project Boilerplates**: Initialize new projects with your boilerplate
- **🔍 Interactive Selection**: Easily choose sessions when multiple are available
- **🔄 Command Re-execution**: Automatically run commands when resuming sessions
- **🧰 Flexible Configurations**: Customize window layouts, pane sizes and commands

---

## 📸 Preview



---

## 📥 Installation

### 🔧 Prerequisites

- Unix-like OS (Linux, macOS, WSL)
- `tmux` installed
- `jq` required for parsing JSON

### 📦 Quick Install

```bash
curl -fsSL https://raw.githubusercontent.com/V-Kleio/DoIt-CLI/main/install.sh | bash
```

### Alternative Installation Methods

#### Manual Install

```bash
git clone https://github.com/V-Kleio/DoIt-CLI.git
cd tmux-doit
./install.sh
```

#### Global Install (All Users)

```bash
curl -fsSL https://raw.githubusercontent.com/V-Kleio/DoIt-CLI/main/install.sh | bash -s -- --global
```

#### Custom Location

```bash
curl -fsSL https://raw.githubusercontent.com/V-Kleio/DoIt-CLI/main/install.sh | bash -s -- --prefix=~/bin
```
> [!IMPORTANT]
> After installation, make sure the installation directory is in your PATH.

---

## 🚀 Usage

### Basic Command Structure
```bash
doit <command> [options] [arguments]
```

### Session Management

**Create a new session**
```bash
doit new <session-name> [template-name]  # Create session with optional template
                                         # Uses 'default' template if not specified
```

> [!WARNING]
> Always review template JSON files from untrusted sources before using them with DoIt.

**Start or attach to an existing session**
```bash
doit start [session-name]                # Start/attach to session
doit start [session-name] --no-run       # Start without re-running commands
```

**Switch between sessions (when already in tmux)**
```bash
doit switch [session-name]               # Switch to session (interactive if no name)
```

**Session listing and status**
```bash
doit list                                # Show all active tmux sessions
```

**Manage sessions**
```bash
doit rename <old-name> <new-name>        # Rename an existing session
doit forget <session-name>               # Kill session and remove from registry
doit clear                               # Kill and forget ALL sessions
```

### Persistence with tmux-resurrect

> [!NOTE]
> For tmux-resurrect integration, make sure tmux-resurrect is installed at `~/.tmux/plugins/tmux-resurrect/`. For more info on how to install: https://github.com/tmux-plugins/tmux-resurrect

**Save and restore sessions**
```bash
doit save                                # Save all sessions with tmux-resurrect
doit restore                             # Restore previously saved sessions
```

### Examples

**Create a development environment**
```bash
# Create a new web project
doit new mywebsite web-dev

# Create a Python project with default template
doit new python-api

# After done with your work, you can save the sessions
doit save
```

**Daily workflow**
```bash
# Start your day by restoring previous sessions
doit restore

# Resume work on a specific project
doit start mywebsite

# Switch between projects (while in tmux)
doit switch python-api
```

For more information, run `doit help` in your terminal.

---

## 📂 Project Structure

```bash
doit/ 
├── bin/ 
│   └── doit # Main executable script 
├── templates/ 
│   └── default.json # Default workspace template 
├── install.sh # Installation script 
├── LICENSE # MIT License 
└── README.md # This file
```

---

## 🛣️ Roadmap

- [x] Core session management functionality
- [x] Template-based workspace creation
- [x] tmux-resurrect integration
- [ ] Template management commands
- [ ] Plugin system for extensions

---

## 🤝 Contributing
All contributions are welcome!
- 🐛 Found a bug?
- ✨ Have a feature idea?
- 🔧 Fixed something yourself?
- 📚 Want to improve the documentation?

Feel free to open an issue or submit a pull request.

Let’s build something awesome together 🚀

---

## 👤 Author

<p align="center"> <a href="https://github.com/V-Kleio"> <img src="https://avatars.githubusercontent.com/u/101655336?v=4" width="100px;" style="border-radius: 50%;" alt="V-Kleio"/> <br /> <sub><b>Rafael Marchel Darma Wijaya</b></sub> </a> </p>
<div align="center" style="color:#6A994E;"> 🌿 Crafted with care | 2025 🌿</div>

<p align="center">
  <a href="https://ko-fi.com/kleiov" target="_blank">
    <img src="https://cdn.ko-fi.com/cdn/kofi3.png?v=3" alt="Ko-fi" style="height: 40px;padding: 20px" />
  </a>
</p>

<!-- ## Contributor

<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/V-Kleio"><img style="border-radius: 20%" src="https://avatars.githubusercontent.com/u/101655336?v=4" width="100px;" alt="V-Kleio"/><br /><sub><b>Rafael Marchel Darma Wijaya</b></sub></a><br /></td>
    </tr>
  </tbody>
</table> -->
