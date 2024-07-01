# Simple-Board
코드 개선 과제
## 구현 기능
- 회원 가입
  - 닉네임은 최소 3자 이상, 알파벳 대소문자(a~z, A~Z), 숫자(0~9)로 구성
  - 비밀번호는 최소 4자 이상이며, 닉네임과 같은 값이 포함된 경우 회원가입 실패
  - 비밀번호 확인과 비밀번호가 정확하게 일치해야 회원 가입 성공
  - 데이터베이스에 존재하는 닉네임을 입력한 채 회원가입 버튼을 누른 경우 "중복된 닉네임입니다." 에러메세지 반환
  - 닉네임 중복 확인
  - 단방향 암호화 알고리즘으로 암호화 후 비밀번호 저장
- 로그인
  - 로그인 버튼을 누른 경우 닉네임과 비밀번호가 데이터베이스에 등록됐는지 확인한 뒤, 하나라도 맞지 않는 정보가 있다면 "닉네임 또는 패스워드를 확인해주세요."라는 에러 메세지 반환
  - 로그인 성공 시 JWT AccessToken 반환
- 전체 게시글 목록 조회
  - 제목, 작성자명(nickname), 작성 날짜를 조회하기
  - 작성 날짜 기준으로 내림차순 정렬하기
  - 페이징 조회 구현
  - 페이징 + 커스텀 정렬 기능 구현하기 -> 사용자가 입력한 key와 정렬 기준을 동적으로 입력 받아, 해당 기준에 맞게 데이터를 제공
- 게시글 작성
  - 토큰을 검사하여, 유효한 토큰일 경우에만 게시글 작성 가능
  - 제목(500자 까지 입력 가능), 작성 내용을 입력하기(5000자 까지 입력 가능)
- 게시글 상세 조회
  - 제목, 작성자명(nickname), 작성 날짜, 작성 내용을 조회하기
- 게시글 수정
  - 토큰을 검사하여, 해당 사용자가 작성한 게시글만 수정 가능
- 게시글 삭제
  - 토큰을 검사하여, 해당 사용자가 작성한 게시글만 삭제 가능
 
## ERD
![image](https://github.com/devitssu/simple-board/assets/63135789/33f91534-d970-4790-9366-00b5dabf57ce)

## API
### 회원 가입
| Method | Path              |
|--------|-------------------|
| `POST` | `/api/v1/sign-up` |

#### 요청
##### Request Body
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `nickname`      | `String`        | 유저명        |
| `email`         | `String`        | 이메일        |
| `password`      | `String`        | 비밀번호       |
| `passwordCheck` | `String`        | 비밀번호 확인   |

#### 응답
##### Status Code
`201 Created`

### 닉네임 중복 확인
| Method | Path              |
|--------|-------------------|
| `GET` | `/api/v1/check-nickname` |

#### 요청
##### Request Body
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `nickname`      | `String`        | 유저명        |

#### 응답
##### Status Code
`200 OK`

### 로그인
| Method | Path              |
|--------|-------------------|
| `POST` | `/api/v1/sign-in` |

#### 요청
##### Request Body
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `nickname`      | `String`        | 유저명        |
| `password`      | `String`        | 비밀번호       |

#### 응답
##### Status Code
`200 OK`
##### Response Body
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `accessToken`   | `String`        | 액세스토큰     |

## API
### 회원 가입
| Method | Path              |
|--------|-------------------|
| `POST` | `/api/v1/sign-up` |

#### 요청
##### Request Body
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `nickname`      | `String`        | 유저명        |
| `email`         | `String`        | 이메일        |
| `password`      | `String`        | 비밀번호       |
| `passwordCheck` | `String`        | 비밀번호 확인   |

#### 응답
##### Status Code
`201 Created`

### 닉네임 중복 확인
| Method | Path              |
|--------|-------------------|
| `GET` | `/api/v1/check-nickname` |

#### 요청
##### Request Body
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `nickname`      | `String`        | 유저명        |

#### 응답
##### Status Code
`200 OK`

### 게시글 작성
| Method | Path              |
|--------|-------------------|
| `POST` | `/api/v1/post` |

#### 요청
##### Request Header
`Authorization: Bearer {accessToken}`

##### Request Body
| Parameter       | Type            | Description | Restriction               |
|-----------------|-----------------|-------------| --------------------------|
| `title`         | `String`        | 제목         | 500자 이하                  |
| `content`       | `String`        | 내용         | 5000자 이하                 |
| `category`      | `String`        | 카테고리      |STUDY, LIFE, WORK, EXERCISE |
| `status`        | `String`        | 상태         |TODO, IN_PROGRESS, DONE    |
| `tagList`       | `List<String>`  | 태그목록      |                           |

#### 응답
##### Status Code
`201 Created`
##### Response Body
| Parameter       | Type            | Description | 
|-----------------|-----------------|-------------| 
| `id`            | `Long`          | ID          |
| `title`         | `String`        | 제목         |
| `content`       | `String`        | 내용         |
| `createdBy`     | `String`        | 작성자명      | 
| `category`      | `String`        | 카테고리      |
| `status`        | `String`        | 상태         |
| `tagList`       | `List<String>`  | 태그목록      |
| `createdAt`     | `LocalTimeDate` | 생성일       |

### 게시글 수정
| Method | Path                   |
|--------|------------------------|
| `PUT` | `/api/v1/post/{postId}` |

#### 요청
##### Request Header
`Authorization: Bearer {accessToken}`

##### Path Parameter
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `postId`         | `Long`         | 게시글 ID     |

##### Request Body
| Parameter       | Type            | Description | Restriction               |
|-----------------|-----------------|-------------| --------------------------|
| `title`         | `String`        | 제목         | 500자 이하                  |
| `content`       | `String`        | 내용         | 5000자 이하                 |
| `category`      | `String`        | 카테고리      |STUDY, LIFE, WORK, EXERCISE |
| `status`        | `String`        | 상태         |TODO, IN_PROGRESS, DONE    |
| `tagList`       | `List<String>`  | 태그목록      |                           |

#### 응답
##### Status Code
`200 OK`
##### Response Body
| Parameter       | Type            | Description | 
|-----------------|-----------------|-------------| 
| `id`            | `Long`          | ID          |
| `title`         | `String`        | 제목         |
| `content`       | `String`        | 내용         |
| `createdBy`     | `String`        | 작성자명      | 
| `category`      | `String`        | 카테고리      |
| `status`        | `String`        | 상태         |
| `tagList`       | `List<String>`  | 태그목록      |
| `createdAt`     | `LocalTimeDate` | 생성일       |

### 게시글 삭제
| Method   | Path                   |
|----------|------------------------|
| `DELETE` | `/api/v1/post/{postId}` |

#### 요청
##### Request Header
`Authorization: Bearer {accessToken}`

##### Path Parameter
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `postId`         | `Long`         | 게시글 ID     |

#### 응답
##### Status Code
`204 NO_CONTENT`

### 게시글 단건 조회
| Method | Path                   |
|--------|------------------------|
| `GET` | `/api/v1/post/{postId}` |

#### 요청
##### Request Header
`Authorization: Bearer {accessToken}`

##### Path Parameter
| Parameter       | Type            | Description |
|-----------------|-----------------|-------------|
| `postId`         | `Long`         | 게시글 ID     |

#### 응답
##### Status Code
`200 OK`
##### Response Body
| Parameter       | Type            | Description | 
|-----------------|-----------------|-------------| 
| `id`            | `Long`          | ID          |
| `title`         | `String`        | 제목         |
| `content`       | `String`        | 내용         |
| `createdBy`     | `String`        | 작성자명      | 
| `category`      | `String`        | 카테고리      |
| `status`        | `String`        | 상태         |
| `tagList`       | `List<String>`  | 태그목록      |
| `createdAt`     | `LocalTimeDate` | 생성일       |

### 게시글 목록 조회
| Method | Path          |
|--------|---------------|
| `GET` | `/api/v1/post` |

#### 요청
##### Request Header
`Authorization: Bearer {accessToken}`

##### Request Body
| Parameter       | Type            | Description | Restriction               |
|-----------------|-----------------|-------------| --------------------------|
| `searchType`    | `String`        | 검색할 타입    |                           |
| `keyword`       | `String`        | 검색할 내용    |                            |
| `category`      | `String`        | 검색할 카테고리 |STUDY, LIFE, WORK, EXERCISE |
| `status`        | `String`        | 검색할 상태    |TODO, IN_PROGRESS, DONE    |
| `tag`           | `String`        | 검색할 태그    |                           |
| `page`          | `Integer`       | 페이지        |                           |
| `size`          | `Integer`       | 페이지 사이즈   |                           |
| `sort`          | `String`        | 정렬 기준     |                           |


#### 응답
##### Status Code
`200 OK`
##### Response Body
```
{
  "totalPages": 0,
  "totalElements": 0,
  "size": 0,
  "content": [
    {
      "id": 0,
      "title": "string",
      "content": "string",
      "createdBy": "string",
      "category": "string",
      "status": "string",
      "tagList": [
        "string"
      ],
      "createdAt": "2024-07-01T03:35:19.811Z"
    }
  ],
  "number": 0,
  "sort": [
    {
      "direction": "string",
      "nullHandling": "string",
      "ascending": true,
      "property": "string",
      "ignoreCase": true
    }
  ],
  "numberOfElements": 0,
  "pageable": {
    "offset": 0,
    "sort": [
      {
        "direction": "string",
        "nullHandling": "string",
        "ascending": true,
        "property": "string",
        "ignoreCase": true
      }
    ],
    "paged": true,
    "unpaged": true,
    "pageNumber": 0,
    "pageSize": 0
  },
  "first": true,
  "last": true,
  "empty": true
}
```
