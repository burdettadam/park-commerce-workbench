# G-0008 browser preview error exercise

Source S: `56141ae40f05704ff0ae30b232ef878e2943b6c6`. The local admin JAR was built from product source `3ededd1ab5d22aa0bebc9cf5d1f155937799807b`; `git diff 3ededd1..S` changes only a pricing integration test and goal records, so the exercised admin product bytes match S.
Time: 2026-10-13 22:36:42 UTC. Browser: Playwright on local loopback Vaadin console `127.0.0.1:8690`.
Input: synthetic 32-character token; visit date 2026-10-12; two adults and two children. No real secret retained.
Endpoint: loopback fixture `127.0.0.1:8782` returned HTTP 400 `INVALID_INPUT` with the known past-date detail; it recorded one POST, 436 request bytes. Fixture source is retained as `checks/preview-error-mock.mjs`.
Observation: the browser's operation result showed the actionable detail. This verifies console rendering and request wiring with a controlled response. The real service's HTTP past-date behavior is separately selected in `JpaPricingIT.httpPreviewPastVisitReturnsTheEmployeeRepairMessageWithoutWrites` and must pass hosted integration at S.

Selected accessibility snapshot lines:
```
- Page URL: http://127.0.0.1:8690/
- Page Title: Park Commerce | Employee workbench
                - generic: Visit date
              - combobox "Visit date" [ref=e129]: 10/12/2026
      - button "Preview unpublished rates" [active] [ref=e158] [cursor=pointer]:
          - generic: Preview unpublished rates
      - heading "Operation result" [level=2] [ref=e160]
      - status [ref=e162]: PREVIEW INPUT — Visit date is in the past in the park time zone
```

