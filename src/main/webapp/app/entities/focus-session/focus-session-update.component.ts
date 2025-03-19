import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import FocusSessionService from './focus-session.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { FocusSession, type IFocusSession } from '@/shared/model/focus-session.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FocusSessionUpdate',
  setup() {
    const focusSessionService = inject('focusSessionService', () => new FocusSessionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const focusSession: Ref<IFocusSession> = ref(new FocusSession());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveFocusSession = async focusSessionId => {
      try {
        const res = await focusSessionService().find(focusSessionId);
        res.createdAt = new Date(res.createdAt);
        focusSession.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.focusSessionId) {
      retrieveFocusSession(route.params.focusSessionId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      duration: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      task: {
        maxLength: validations.maxLength('本字段最大长度为 200 个字符.', 200),
      },
      date: {
        required: validations.required('本字段不能为空.'),
      },
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, focusSession as any);
    v$.value.$validate();

    return {
      focusSessionService,
      alertService,
      focusSession,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: focusSession }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.focusSession.id) {
        this.focusSessionService()
          .update(this.focusSession)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A FocusSession is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.focusSessionService()
          .create(this.focusSession)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A FocusSession is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
