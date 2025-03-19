import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import MoodTrackerUpdate from './mood-tracker-update.vue';
import MoodTrackerService from './mood-tracker.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type MoodTrackerUpdateComponentType = InstanceType<typeof MoodTrackerUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const moodTrackerSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MoodTrackerUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MoodTracker Management Update Component', () => {
    let comp: MoodTrackerUpdateComponentType;
    let moodTrackerServiceStub: SinonStubbedInstance<MoodTrackerService>;

    beforeEach(() => {
      route = {};
      moodTrackerServiceStub = sinon.createStubInstance<MoodTrackerService>(MoodTrackerService);
      moodTrackerServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          moodTrackerService: () => moodTrackerServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(MoodTrackerUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MoodTrackerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.moodTracker = moodTrackerSample;
        moodTrackerServiceStub.update.resolves(moodTrackerSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(moodTrackerServiceStub.update.calledWith(moodTrackerSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        moodTrackerServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MoodTrackerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.moodTracker = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(moodTrackerServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        moodTrackerServiceStub.find.resolves(moodTrackerSample);
        moodTrackerServiceStub.retrieve.resolves([moodTrackerSample]);

        // WHEN
        route = {
          params: {
            moodTrackerId: `${moodTrackerSample.id}`,
          },
        };
        const wrapper = shallowMount(MoodTrackerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.moodTracker).toMatchObject(moodTrackerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        moodTrackerServiceStub.find.resolves(moodTrackerSample);
        const wrapper = shallowMount(MoodTrackerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
