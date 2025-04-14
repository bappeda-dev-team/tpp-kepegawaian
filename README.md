# TPP Kepegawaian Service

Aplikasi ini adalah bagian dari sistem aplikasi KertasKerja dan menyediakan layanan untuk pengelolaan data Kepegawaian.

## REST API
| Endpoint       | Method | Query Params                 | Req. Body            | Status | Resp. Body      | Description                         |
|----------------|--------|------------------------------|----------------------|--------|-----------------|-------------------------------------|
| `/kepegawaians`| GET    | `kode_opd`, `bulan`, `tahun` | -                    | 200    | KepegawaianList | Filter pegawai by OPD, tahun bulan. |
| `/kepegawaians`| POST   | -                            | KepegawaianRequest   | 201    | Kepegawaian     | Input Pegawai baru                  |
