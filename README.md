<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="ShareItIcon.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Share It</h3>

  <p align="center">
    Сервис для шеринга вещей
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Содержание</summary>
  <ol>
    <li>
      <a href="#о-проекте">О проекте</a>
      <ul>
        <li><a href="#создан-при-помощи">Создан при помощи</a></li>
      </ul>
    </li>
    <li>
      <a href="#запуск-проекта">Запуск проекта</a>
      <ul>
        <li><a href="#приготовления">Приготовления</a></li>
        <li><a href="#установка">Установка</a></li>
      </ul>
    </li>
    <li><a href="#использование">Использование</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## О проекте

Мой первый проект основанный на микросервисной архитектуре с использованием Docker контейнеров. Проект представляет собой сервис для сдачи предметов в аренду между пользователями. 

Сервис предоставляет следующий функционал:
* Регистрация пользователей, получение информации о уже зарегестрированных пользователях
* Добавление предметов и заявок на их аренду
* Комментирование успешно завершённой аренды
* Возможность оставить запрос с описанием желаемого предмета

Проект состоит из 3-х микросервисов: Gateway - валидация запросов, Server - бизнес логика, DB - база данных. Каждый микросервис поднимается в своём докер контейнере.

<p align="right">(<a href="#readme-top">к заглавию</a>)</p>



### Создан при помощи

* ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
* ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
* ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
* ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

<p align="right">(<a href="#readme-top">к заглавию</a>)</p>



<!-- GETTING STARTED -->
## Запуск проекта

Далее описаны требования для запуска проекта на локальной машине:

### Приготовления

Для работы приложение требуется установленный и запущенный Docker daemon. Для проверки его наличия введите следующую команду в консоли (Windows)
* cmd
  ```sh
  docker version
  ```
Если выводится информация о установленной системе, переходим к следующему шагу.

### Установка

_Далее описаны пункты для запуска проекта_

1. Клонируйте репозиторий
   ```cmd
   git clone https://github.com/LeonovAlexProg/java-shareit.git
   ```
2. Перейдите в корневую папку проекта
   ```cmd
   cd {путь да корневой директории}/ShareIt
   ```
4. Вызовите утилиту что бы поднять все контейнеры
   ```cmd
   docker-compose up
   ```

<p align="right">(<a href="#readme-top">к заглавию</a>)</p>



<!-- USAGE EXAMPLES -->
## Использование

_Postman-коллекция с примерами запросов - [Ссылка]([https://example.com](https://github.com/yandex-praktikum/java-shareit/blob/add-docker/postman/sprint.json))_

<p align="right">(<a href="#readme-top">к заглавию</a>)</p>

<!-- CONTACT -->
## Contact

Леонов АЛександр - Телеграм(@b4klazhan) - leonovalexprog@gmail.com

Ссылка на проект: [https://github.com/LeonovAlexProg/java-shareit](https://github.com/LeonovAlexProg/java-shareit)

<p align="right">(<a href="#readme-top">к заглавию</a>)</p>
