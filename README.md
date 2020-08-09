# 오늘뭐입지_날씨매칭 의상추천 서비스

### Firebase 인증·DB·저장소 를 활용한 Android-Native 애플리케이션

<br>

<br>

### [📝Notion에서 프로젝트 보고서 읽기](https://www.notion.so/_-Android-Native-Firebase-DB-3ef05fc5f57a43ac91220c98228287eb)

### [📲Google Play에서 설치하기](https://play.google.com/store/apps/details?id=com.hyunro.wtwt)

<br>

<br>

### 목차

1. 프로젝트 개괄
2. 시스템 구성 및 사용 기술
4. 액티비티맵

<br>

<br>

---

# 1. 프로젝트 개괄

### 1.1 '오늘뭐입지' 서비스 개요

: 날씨매칭 의상추천 서비스

<br><br>

**1. 오늘 날씨에 적절한 의상 추천**

- 다른 사용자들이 업로드한 의상 정보 기반
- `오늘 날씨` 외에 사용자 `성별` · `나이`, `기준지역` 등을 기준으로 추천

![https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fad732f39-4ea7-4a9d-a3f1-80953541e1f2%2Fwtwt_etc1.png?table=block&id=b17033ec-dca8-4571-b363-245f7605dd82&width=1870&cache=v2](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fad732f39-4ea7-4a9d-a3f1-80953541e1f2%2Fwtwt_etc1.png?table=block&id=b17033ec-dca8-4571-b363-245f7605dd82&width=1870&cache=v2)

<br><br>

**2. 사용자의 오늘 의상정보 업로드**
- 업로드시 오늘의 날씨 정보도 함께 저장 (∵추천 알고리즘 적용)

![https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F7a7c9a7f-c8d9-4e64-aa67-db74cf416b02%2Fwtwt_etc2.png?table=block&id=1dc4509c-400b-46e1-ab21-634cf795638e&width=1870&cache=v2](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F7a7c9a7f-c8d9-4e64-aa67-db74cf416b02%2Fwtwt_etc2.png?table=block&id=1dc4509c-400b-46e1-ab21-634cf795638e&width=1870&cache=v2)

<br><br>

### 1.3 프로젝트 인원 및 기간

- **1인** 프로젝트 (`서비스 기획` / `시스템 설계` / `개발 수행` / `디자인` / `보고서 작성` 등)
- **1개월** (2020.2.5 ~ 2020.3.6)

<br><br>

---

# 2. 시스템 구성 및 사용기술

### 2.1 시스템 구성도

![https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fcdbe0e58-698d-4b65-a568-aea543fcc545%2F_009.png?table=block&id=365d3ab4-a57f-4f17-9821-11a5fd1b2c85&width=2220&cache=v2](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fcdbe0e58-698d-4b65-a568-aea543fcc545%2F_009.png?table=block&id=365d3ab4-a57f-4f17-9821-11a5fd1b2c85&width=2220&cache=v2)

1. 날씨 데이터 수집 서버
: `기상청 오픈API 동네예보조회` , `자바 프로그램 Cron Job on AWS`
2. 데이터 서버 (사용자인증 · DB · 저장소)
: Google Firebase `Auth` · `Cloud Firestore(NoSQL)` · `Cloud Storage`
3. 클라이언트 안드로이드 애플리케이션
: `Android-Native` (Marshmallow 이상 지원)

<br><br>

### 2.2 안드로이드 애플리케이션 기능 스펙

![https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F8584e568-19e2-4032-99b1-465c1fa1b78f%2F_039.png?table=block&id=8893bbd7-3f91-413e-b362-24914dca78de&width=1980&cache=v2](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F8584e568-19e2-4032-99b1-465c1fa1b78f%2F_039.png?table=block&id=8893bbd7-3f91-413e-b362-24914dca78de&width=1980&cache=v2)

<br><br>

### 2.3 날씨 데이터 수집 프로그램 기능

- **기상청 오픈 API '동네예보조회' 특징**

    - `02, 05, 08, 11, 14, 17, 20, 23시`마다(8회/日), `03, 06, 09, 12, 15, 18, 21, 24시` 기준의 날씨 데이터 제공

    - 조회시간기준 `-24시간`~`+72시간` 동안의 날씨 데이터만 제공

- **AWS서버에서 날씨 데이터 수집 프로그램 동작(cron job, 8회/日)**

    - 기상청 API에서 조회한 데이터를 Firebase 자체 DB에 저장
    (∵ ******`-24시간` 이전의 과거 날씨 데이터 서비스 제공 목적)

<br><br>

---

# 3. 액티비티맵

![https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F53d36e77-6cc0-4c96-a6ea-1403398822dc%2FActivityMap.png?table=block&id=7b2f61bb-1bc6-4c31-ab98-1132378471ef&width=3700&cache=v2](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F53d36e77-6cc0-4c96-a6ea-1403398822dc%2FActivityMap.png?table=block&id=7b2f61bb-1bc6-4c31-ab98-1132378471ef&width=3700&cache=v2)

*안드로이드 4대 구성요소

- 액티비티 : ①~⑨
- 서비스 : ⑩
- 브로드캐스트 리시버 : ⑪~⑫
- 컨텐트프로바이더 : ⑬

<br><br><br><br>