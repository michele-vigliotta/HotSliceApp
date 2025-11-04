<h1 align="center">HotSlice - Pizza Restaurant Management App</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Language-Kotlin-purple.svg" alt="Kotlin">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Android">
  <img src="https://img.shields.io/badge/Backend-Firebase-orange.svg" alt="Firebase">
  <img src="https://img.shields.io/badge/Architecture-MVVM-blue.svg" alt="MVVM">
</p>


<p>
Mobile application for managing pizza restaurant operations, developed in both Kotlin (Android) and Flutter (cross-platform).
</p>

<hr>

<p align="center">
<img src="docs/main_view.png" width="500">
</p>
  

<h2>Application Structure</h2>

<p>
The app is organized into three user roles, each with dedicated functionality accessed through a shared bottom navigation bar. After login, users are redirected to their role-specific homepage.
</p>

<ol>
  <li><b>Cliente (Customer)</b>: menu browsing, cart, orders, favorites</li>
  <li><b>Staff</b>: offer management, order handling (dine-in/takeaway)</li>
  <li><b>Admin</b>: full menu CRUD, sales analytics, best-seller statistics</li>
</ol>


<h2>Features</h2>

  - <b>Dynamic Navigation</b>:  single MainActivity with role-based bottom bar; fragment-based navigation
  - <b>Menu Management (Admin)</b>: add/edit/delete products with image upload to Firebase Storage
  - <b>Order System</b>: real-time status updates; pickup time validation (19:00-24:00 for takeaway)
  - <b>Statistics Dashboard (Admin)</b>: bar charts with temporal filters (weekly/monthly/yearly)
  - <b>State Management</b>: ViewModel pattern (Kotlin), Provider pattern (Flutter)


<h2>Tech Stack</h2>

  - <b>Frontend</b>: Kotlin (Android), Flutter/Dart (cross-platform)
  - <b>Backend</b>: Firebase (Authentication, Firestore, Storage)
  - <b>Database</b>: NoSQL - Firestore
  - <b>Architecture</b>: MVVM (Kotlin), Provider + StatefulWidgets (Flutter)
 
 
<h2>Documentation</h2>
<p>
Full technical documentation in the repository root:
<code>HotSliceAppRelazione.pdf</code>
</p>

<h2>Authors</h2>

* Michele Vigliotta
* Filippo Montagnoli
* Giovanni Prati

<hr>

<p align="center"><i>University project developed for the Mobile Programming (Programmazione Mobile) course.</i></p>

