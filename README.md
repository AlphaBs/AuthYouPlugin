# AuthYouPlugin

Plugin for AuthYou game server

## Configuration

경로: `AuthYou/config.json`  
JSON 형식으로 저장  

| 이름                 | 형식     | 기본값               | 설명 |
|----------------------|----------|----------------------|---------------|
| host                 | String   |                      | 인증 서버 주소 |
| serverId             | String   |                      | 서버 아이디 |
| requestTimeout       | Number   | 10000                | 인증 타임아웃 |
| allowLocalIp         | Bool     | false                | 루프백 아이피, 내부 아이피로 접속하는 유저를 항상 허용할 지 여부 |
| passOnError          | Bool     | false                | 서버 오류 발생 시 접속하는 유저들을 모두 허용할 지 여부 |
| useDetailKickMessage | Bool     | false                | 유저를 추방시킬 때 자세한 에러 메세지를 표시할 지 여부 |
| checkDelaySec        | Number   | 5                    | (Bungeecord 플러그인 전용) 접속 후 인증 시작까지 딜레이 (초단위) |
| checkDelayTick       | Number   | 100                  | (Spigot 플러그인 전용) 접속 후 인증 시작까지 딜레이 (틱단위) |
| kickMessage          | String   | AuthYou Kick Message | 인증되지 않은 유저가 접속했을 때 킥 메세지 |
| allowUser            | String[] |                      | 항상 허용할 유저의 목록 |

## Commands

### list
usage: `/authoyu list`  
description: 허용 유저 목록 출력  

### addname
usage: `/authyou addname <username>`  
description: 허용 유저 목록에 유저이름 추가. 접속한 유저의 이름만 입력 가능  

### removename
usage: `/authyou removename <username>`  
description: 허용 유저 목록에 유저이름 제거. 접속한 유저의 이름만 입력 가능  

### adduuid
usage: `/authyou adduuid <uuid>`  
description: 허용 유저 목록에 UUID 추가  

### removeuuid
usage: `/authyou removeuuid <uuid>`  
description: 허용 유저 목록에 UUID 제거  
