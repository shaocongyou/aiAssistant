import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MoodTrackerService from './mood-tracker.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type IMoodTracker, MoodTracker } from '@/shared/model/mood-tracker.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MoodTrackerUpdate',
  setup() {
    const moodTrackerService = inject('moodTrackerService', () => new MoodTrackerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const moodTracker: Ref<IMoodTracker> = ref(new MoodTracker());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMoodTracker = async moodTrackerId => {
      try {
        const res = await moodTrackerService().find(moodTrackerId);
        res.createdAt = new Date(res.createdAt);
        moodTracker.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.moodTrackerId) {
      retrieveMoodTracker(route.params.moodTrackerId);
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
      mood: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 50 个字符.', 50),
      },
      intensity: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      date: {
        required: validations.required('本字段不能为空.'),
      },
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, moodTracker as any);
    v$.value.$validate();

    return {
      moodTrackerService,
      alertService,
      moodTracker,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: moodTracker }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.moodTracker.id) {
        this.moodTrackerService()
          .update(this.moodTracker)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A MoodTracker is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.moodTrackerService()
          .create(this.moodTracker)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A MoodTracker is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
